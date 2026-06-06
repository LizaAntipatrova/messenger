package org.strongcat.taskservice.service;

import org.springframework.stereotype.Component;
import org.strongcat.taskservice.data.entity.SpecialistSkill;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MatchingCalculator {

    // Используем MathContext для точных вычислений с BigDecimal (до 5 знаков после запятой)
    private static final MathContext MC = new MathContext(5);

    public BigDecimal calculateCosineSimilarity(List<Long> requestSkillIds, List<SpecialistSkill> specialistSkills) {
        if (requestSkillIds == null || requestSkillIds.isEmpty() || specialistSkills == null || specialistSkills.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Множество ID навыков специалиста для быстрого поиска O(1)
        Set<Long> specSkillIds = specialistSkills.stream()
                .map(ss -> ss.getSkill().getId())
                .collect(Collectors.toSet());

        // 1. Скалярное произведение векторов (Dot Product)
        // Так как веса у нас равны 1.0, совпадение — это просто +1 к сумме
        long intersectionCount = requestSkillIds.stream()
                .filter(specSkillIds::contains)
                .count();

        if (intersectionCount == 0) {
            return BigDecimal.ZERO;
        }

        // 2. Длина вектора задачи (норма вектора A)
        // Квадратный корень из суммы квадратов координат. Координаты = 1, значит сумма = количеству навыков.
        double normA = Math.sqrt(requestSkillIds.size());

        // 3. Длина вектора специалиста (норма вектора B)
        double normB = Math.sqrt(specialistSkills.size());

        // 4. Итоговый расчет косинусной близости
        double similarity = intersectionCount / (normA * normB);

        return new BigDecimal(similarity, MC);
    }
}