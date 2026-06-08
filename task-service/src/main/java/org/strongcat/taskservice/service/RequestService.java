package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.strongcat.taskservice.data.entity.*;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.enums.ResponseStatusName;
import org.strongcat.taskservice.data.repository.*;
import org.strongcat.taskservice.dto.CreateRequestDto;
import org.strongcat.taskservice.dto.internal.TaskDistributionRecipientDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;
    private final RequestSkillRepository requestSkillRepository;
    private final MatchingService matchingService;
    private final RequestRecipientRepository requestRecipientRepository;
    private final ResponseStatusRepository responseStatusRepository;
    private final RequestDistributionService requestDistributionService;

    @Transactional
    public Long createRequest(CreateRequestDto dto, Long initiatorId) {
        // 1. Находим специализацию
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Специализация не найдена с id: " + dto.getSpecializationId()));

        // 2. Находим дефолтный статус CREATED
        RequestStatus defaultStatus = requestStatusRepository.findByName(RequestStatusName.CREATED.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус 'CREATED' не инициализирован в БД"));

        // 3. Маппим и сохраняем саму Заявку
        Request request = new Request();
        request.setInitiatorId(initiatorId);
        request.setSpecialization(specialization);
        request.setDescription(dto.getDescription());
        request.setRequiredExperience(dto.getRequiredExperience());
        request.setPayment(dto.getPayment());
        request.setExpectedDurationDays(dto.getExpectedDurationDays());
        request.setRequestStatus(defaultStatus);

        Request savedRequest = requestRepository.save(request);

        // 4. Сохраняем требуемые навыки задачи (веса по умолчанию = 1.0)
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            List<RequestSkill> requestSkills = dto.getSkillIds().stream()
                    .map(skillId -> {
                        Skill skill = skillRepository.findById(skillId)
                                .orElseThrow(() -> new IllegalArgumentException("Навык не найден с id: " + skillId));

                        RequestSkill requestSkill = new RequestSkill();
                        requestSkill.setRequest(savedRequest);
                        requestSkill.setSkill(skill);
                        requestSkill.setSkillWeight(BigDecimal.ONE);
                        return requestSkill;
                    }).toList();

            requestSkillRepository.saveAll(requestSkills);
        }

        // 5. АВТОМАТИЧЕСКАЯ АДРЕСАЦИЯ: Запуск алгоритма ранжирования
        List<RequestRecipient> bestCandidates = matchingService.findBestSpecialistsForRequest(savedRequest, dto.getSkillIds());

        if (bestCandidates.isEmpty()) {
            // Если жесткие фильтры или косинусная близость никого не пропустили,
            // переводим задачу в статус NO_CANDIDATES
            RequestStatus noCandidatesStatus = requestStatusRepository.findByName(RequestStatusName.NO_CANDIDATES.getDatabaseName())
                    .orElseThrow(() -> new IllegalStateException("Статус 'NO_CANDIDATES' не инициализирован в БД"));

            savedRequest.setRequestStatus(noCandidatesStatus);
            requestRepository.save(savedRequest);
        } else {
            // Если кандидаты найдены, получаем статус отклика 'SENT' (запрос отправлен специалисту)
            ResponseStatus sentResponseStatus = responseStatusRepository.findByName(ResponseStatusName.SENT.getDatabaseName())
                    .orElseThrow(() -> new IllegalStateException("Статус отклика 'SENT' не инициализирован в БД"));

            // Проставляем статус отклика и время отправки для каждого кандидата
            for (RequestRecipient recipient : bestCandidates) {
                recipient.setResponseStatus(sentResponseStatus);
                recipient.setSentAt(LocalDateTime.now());
            }

            List<RequestRecipient> savedRecipients = requestRecipientRepository.saveAll(bestCandidates);
            scheduleTaskDistribution(savedRequest, savedRecipients);
        }

        return savedRequest.getId();
    }

    private void scheduleTaskDistribution(Request request, List<RequestRecipient> recipients) {
        Long requestId = request.getId();
        Long initiatorId = request.getInitiatorId();
        String description = request.getDescription();
        BigDecimal payment = request.getPayment();
        String specializationName = request.getSpecialization().getName();
        List<TaskDistributionRecipientDto> recipientDtos = recipients.stream()
                .map(recipient -> TaskDistributionRecipientDto.builder()
                        .recipientId(recipient.getId())
                        .specialistExternalUserId(recipient.getSpecialist().getExternalUserId())
                        .rankPosition(recipient.getRankPosition())
                        .build())
                .toList();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                requestDistributionService.distributeRequestToSpecialists(
                        requestId, initiatorId, description, payment, specializationName, recipientDtos);
            }
        });
    }

    @Transactional(readOnly = true)
    public String getRequestStatus(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена с id: " + requestId));

        // Берем имя из сущности-статуса задачи
        return request.getRequestStatus().getName();
    }

    /**
     * Получить статус отклика конкретного специалиста на задачу
     */
    @Transactional(readOnly = true)
    public String getResponseStatus(Long requestId, Long externalUserId) {
        RequestRecipient recipient = requestRecipientRepository.findByRequestIdAndSpecialistExternalUserId(requestId, externalUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Откликов для специалиста %d по задаче %d не найдено", externalUserId, requestId)
                ));

        // Берем имя из сущности-статуса отклика
        return recipient.getResponseStatus().getName();
    }

    @Transactional(readOnly = true)
    public Optional<Long> getAcceptedSpecialistExternalUserId(Long requestId) {
        return requestRecipientRepository.findAcceptedRecipientByRequestId(requestId)
                .map(recipient -> recipient.getSpecialist().getExternalUserId());
    }
}