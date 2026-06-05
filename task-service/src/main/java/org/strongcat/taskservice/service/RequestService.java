package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.*;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.repository.*;
import org.strongcat.taskservice.dto.CreateRequestDto;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;
    private final RequestSkillRepository requestSkillRepository;

    @Transactional
    public Long createRequest(CreateRequestDto dto) {
        // 1. Ищем специализацию
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Специализация не найдена с id: " + dto.getSpecializationId()));

        // 2. Ищем дефолтный статус CREATED
        RequestStatus defaultStatus = requestStatusRepository.findByName(RequestStatusName.CREATED.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус 'CREATED' не инициализирован в БД"));

        // 3. Маппим и сохраняем саму Заявку
        Request request = new Request();
        request.setInitiatorId(dto.getInitiatorId());
        request.setSpecialization(specialization);
        request.setDescription(dto.getDescription());
        request.setRequiredExperience(dto.getRequiredExperience());
        request.setPayment(dto.getPayment());
        request.setExpectedDurationDays(dto.getExpectedDurationDays());
        request.setRequestStatus(defaultStatus);

        Request savedRequest = requestRepository.save(request);

        // 4. Сохраняем навыки, если они были переданы
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            List<RequestSkill> requestSkills = dto.getSkillIds().stream()
                    .map(skillId -> {
                        Skill skill = skillRepository.findById(skillId)
                                .orElseThrow(() -> new IllegalArgumentException("Навык не найден с id: " + skillId));

                        RequestSkill requestSkill = new RequestSkill();
                        requestSkill.setRequest(savedRequest);
                        requestSkill.setSkill(skill);
                        requestSkill.setSkillWeight(BigDecimal.ONE); // Выставляем дефолтный вес = 1.0
                        return requestSkill;
                    }).toList();

            requestSkillRepository.saveAll(requestSkills);
        }

        return savedRequest.getId();
    }
}