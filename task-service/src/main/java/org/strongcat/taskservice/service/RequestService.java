package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.*;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.enums.ResponseStatusName;
import org.strongcat.taskservice.data.repository.*;
import org.strongcat.taskservice.dto.CreateRequestDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;
    private final RequestSkillRepository requestSkillRepository;

    // Внедряем наш вычислительный сервис подбора
    private final MatchingService matchingService;
    private final RequestRecipientRepository requestRecipientRepository;
    private final ResponseStatusRepository responseStatusRepository;

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

            // Сохраняем массив адресатов в базу данных
            requestRecipientRepository.saveAll(bestCandidates);

            // TODO позже: Здесь будет вызывать метод другого микросервиса для отправки событий в Kafka
            // другойМикросервисClient.sendNotifications(bestCandidates);
        }

        return savedRequest.getId();
    }
}