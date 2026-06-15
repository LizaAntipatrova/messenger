package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.*;
import org.strongcat.taskservice.data.repository.SkillRepository;
import org.strongcat.taskservice.data.repository.SpecialistRepository;
import org.strongcat.taskservice.data.repository.SpecialistSkillRepository;
import org.strongcat.taskservice.data.repository.SpecializationRepository;
import org.strongcat.taskservice.dto.RegisterSpecialistDto;
import org.strongcat.taskservice.dto.internal.LlmProfileResponse;
import org.strongcat.taskservice.dto.internal.LlmSkillResponse;
import org.strongcat.taskservice.dto.internal.LlmTaskResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final SpecializationRepository specializationRepository;
    private final SpecialistSkillRepository specialistSkillRepository;
    private final SkillRepository skillRepository;
    private final OpenAiService llmService;

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

        List<String> skillDict = skillRepository.findAll().stream().map(Skill::getName).toList();
        LlmProfileResponse llmProfileResponse = llmService.analyzeProfile(dto.getDescription(), skillDict);
        if(dto.getMinPayment() == null && llmProfileResponse.getMinRateRub() == null) {
            specialist.setMinPayment(new BigDecimal(100));
        } else if (dto.getMinPayment() == null) {
            specialist.setMinPayment(llmProfileResponse.getMinRateRub());

        }
        if(dto.getExperienceMonths() == null && llmProfileResponse.getExperienceMonths() == null) {
            specialist.setExperienceMonths(0);
        } else if (dto.getExperienceMonths() == null) {
            specialist.setExperienceMonths(llmProfileResponse.getExperienceMonths());

        }

        // 4. Сохраняем требуемые навыки задачи (веса по умолчанию = 0.8)

        Specialist savedSpecialist = specialistRepository.save(specialist);
        replaceSkills(savedSpecialist, dto.getSkillIds(), llmProfileResponse.getSkills());

        return savedSpecialist.getId();
    }

    private void replaceSkills(Specialist specialist, List<Long> skillIds, List<LlmSkillResponse> llmSkills) {
        specialistSkillRepository.deleteAllBySpecialistId(specialist.getId());

        Set<SpecialistSkill> newSkills =  new HashSet<>();
        // 4. Сохраняем требуемые навыки задачи (веса по умолчанию = 0.8)
        if (skillIds != null && !skillIds.isEmpty()) {
            newSkills = skillIds.stream()
                    .map(skillId -> {
                        Skill skill = skillRepository.findById(skillId)
                                .orElseThrow(() -> new IllegalArgumentException("Навык не найден с id: " + skillId));

                        SpecialistSkill specialistSkill = new SpecialistSkill();
                        specialistSkill.setSpecialist(specialist);
                        specialistSkill.setSkill(skill);
                        specialistSkill.setSkillWeight(new BigDecimal("0.8"));
                        return specialistSkill;
                    }).collect(Collectors.toSet());
        }
        if (llmSkills != null && !llmSkills.isEmpty()) {
            newSkills.addAll(llmSkills.stream()
                    .map(llmSkill -> {
                        Optional<Skill> skillOpt = skillRepository.findByName(llmSkill.getName());
                        if (skillOpt.isEmpty()){
                            return null;
                        }
                        Skill skill = skillOpt.get();
                        SpecialistSkill specialistSkill = new SpecialistSkill();
                        specialistSkill.setSpecialist(specialist);
                        specialistSkill.setSkill(skill);
                        specialistSkill.setSkillWeight(llmSkill.getWeight());
                        return specialistSkill;
                    }).filter(Objects::nonNull).collect(Collectors.toSet()));
        }



        specialistSkillRepository.saveAll(newSkills);
    }
}
