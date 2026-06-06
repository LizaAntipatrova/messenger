package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.Skill;
import org.strongcat.taskservice.data.entity.Specialist;
import org.strongcat.taskservice.data.entity.SpecialistSkill;
import org.strongcat.taskservice.data.entity.Specialization;
import org.strongcat.taskservice.data.repository.SkillRepository;
import org.strongcat.taskservice.data.repository.SpecialistRepository;
import org.strongcat.taskservice.data.repository.SpecialistSkillRepository;
import org.strongcat.taskservice.data.repository.SpecializationRepository;
import org.strongcat.taskservice.dto.RegisterSpecialistDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final SpecializationRepository specializationRepository;
    private final SpecialistSkillRepository specialistSkillRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public Long registerOrUpdateSpecialist(RegisterSpecialistDto dto) {
        if (dto.getSpecializationName() == null || dto.getSpecializationName().isBlank()) {
            throw new IllegalArgumentException("Имя специализации не может быть пустым");
        }

        String formattedSpecName = dto.getSpecializationName().trim();

        Specialization specialization = specializationRepository.findByName(formattedSpecName)
                .orElseThrow(() -> new IllegalArgumentException("Специализация не найдена с именем: "
                        + dto.getSpecializationName()));

        Specialist specialist = specialistRepository.findByExternalUserId(dto.getExternalUserId())
                .orElseGet(() -> {
                    Specialist newSpec = new Specialist();
                    newSpec.setExternalUserId(dto.getExternalUserId());
                    return newSpec;
                });

        specialist.setSpecialization(specialization);
        specialist.setExperienceMonths(dto.getExperienceMonths());
        specialist.setMinPayment(dto.getMinPayment() != null ? dto.getMinPayment() : BigDecimal.ONE);
        specialist.setLastActivityAt(dto.getLastActivityAt() != null ? dto.getLastActivityAt() : LocalDateTime.now());
        specialist.setAvgTaskCost(dto.getAvgTaskCost() != null ? dto.getAvgTaskCost() : BigDecimal.ONE);
        specialist.setPreferredDifficultyMonths(
                dto.getPreferredDifficultyMonths() != null ? dto.getPreferredDifficultyMonths() : 0);

        Specialist savedSpecialist = specialistRepository.save(specialist);
        replaceSkills(savedSpecialist, dto.getSkillIds());
        return savedSpecialist.getId();
    }

    private void replaceSkills(Specialist specialist, List<Long> skillIds) {
        specialistSkillRepository.deleteAllBySpecialistId(specialist.getId());

        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }

        List<SpecialistSkill> newSkills = skillIds.stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new IllegalArgumentException("Навык не найден с id: " + skillId));

                    SpecialistSkill specialistSkill = new SpecialistSkill();
                    specialistSkill.setSpecialist(specialist);
                    specialistSkill.setSkill(skill);
                    specialistSkill.setSkillWeight(java.math.BigDecimal.ONE);
                    return specialistSkill;
                }).toList();

        specialistSkillRepository.saveAll(newSkills);
    }
}
