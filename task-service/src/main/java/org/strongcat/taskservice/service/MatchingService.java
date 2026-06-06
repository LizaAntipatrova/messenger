package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.strongcat.taskservice.data.entity.*;
import org.strongcat.taskservice.data.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final SpecialistRepository specialistRepository;
    private final SpecialistSkillRepository specialistSkillRepository;
    private final MatchingCalculator matchingCalculator;
    private final CorrectionFactorCalculator factorCalculator;

    // Константа M — сколько максимум кандидатов мы хотим отобрать для адресации (из ТЗ вашей ВКР)
    private static final int TOP_M_CANDIDATES = 5; 

    public List<RequestRecipient> findBestSpecialistsForRequest(Request request, List<Long> requestSkillIds) {
        
        // 1. Выполняем жесткую предварительную фильтрацию (Hard Filters)
        List<Specialist> filteredSpecialists = specialistRepository.findPotentialCandidates(
                request.getSpecialization().getId(),
                request.getPayment(),
                request.getRequiredExperience()
        );

        if (filteredSpecialists.isEmpty()) {
            return new ArrayList<>();
        }

        // Извлекаем ID отфильтрованных специалистов для пакетного запроса навыков
        List<Long> specialistIds = filteredSpecialists.stream().map(Specialist::getId).toList();

        // 2. Выгружаем навыки отобранных специалистов одним запросом и группируем их по specialist_id
        List<SpecialistSkill> allSkills = specialistSkillRepository.findAllBySpecialistIds(specialistIds);
        Map<Long, List<SpecialistSkill>> skillsBySpecialist = allSkills.stream()
                .collect(Collectors.groupingBy(ss -> ss.getSpecialist().getId()));

        List<RequestRecipient> candidatesProposal = new ArrayList<>();

        // 3. Считаем метрики для каждого кандидата
        for (Specialist specialist : filteredSpecialists) {
            List<SpecialistSkill> specialistSkills = skillsBySpecialist.getOrDefault(specialist.getId(), new ArrayList<>());

            // Рассчитываем косинусную близость (Шаг 2)
            BigDecimal cosineSimilarity = matchingCalculator.calculateCosineSimilarity(requestSkillIds, specialistSkills);
            
            // Если совпадений по навыкам вообще нет (0.0), сразу пропускаем кандидата
            if (cosineSimilarity.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            // Рассчитываем корректирующий коэффициент (Шаг 3)
            BigDecimal correctionCoefficient = factorCalculator.calculateCoefficient(specialist);

            // Итоговый балл = Косинусная близость * Коэффициент
            BigDecimal finalScore = cosineSimilarity.multiply(correctionCoefficient);

            // Формируем объект адресации (пока без сохранения в БД)
            RequestRecipient recipient = new RequestRecipient();
            recipient.setRequest(request);
            recipient.setSpecialist(specialist);
            recipient.setCosineSimilarity(cosineSimilarity);
            recipient.setCorrectionCoefficient(correctionCoefficient);
            recipient.setFinalScore(finalScore);
            
            candidatesProposal.add(recipient);
        }

        // 4. Ранжируем кандидатов по убыванию Final Score и присваиваем позиции (Rank)
        candidatesProposal.sort(Comparator.comparing(RequestRecipient::getFinalScore).reversed());

        // Оставляем только TOP-M лучших кандидатов, чтобы избежать информационного шума
        List<RequestRecipient> finalRecipients = candidatesProposal.stream()
                .limit(TOP_M_CANDIDATES)
                .toList();

        // Проставляем порядковый ранг (1, 2, 3...)
        for (int i = 0; i < finalRecipients.size(); i++) {
            finalRecipients.get(i).setRankPosition(i + 1);
        }

        return finalRecipients;
    }
}