package org.strongcat.taskservice.service;

import org.springframework.stereotype.Component;
import org.strongcat.taskservice.data.entity.RequestSkill;
import org.strongcat.taskservice.data.entity.SpecialistSkill;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MatchingCalculator {

    // Используем MathContext для точных вычислений с BigDecimal (до 5 знаков после запятой)
    private static final MathContext MC = new MathContext(5);

    public BigDecimal calculateCosineSimilarity(List<RequestSkill> requestSkills, List<SpecialistSkill> specialistSkills) {
        if (requestSkills == null || requestSkills.isEmpty() || specialistSkills == null || specialistSkills.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<Long, BigDecimal> requestVector = requestSkills.stream()
                .collect(Collectors.toMap(
                        rs -> rs.getSkill().getId(),
                        RequestSkill::getSkillWeight,
                        BigDecimal::max
                ));

        Map<Long, BigDecimal> specialistVector = specialistSkills.stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        SpecialistSkill::getSkillWeight,
                        BigDecimal::max
                ));
        BigDecimal dotProduct = BigDecimal.ZERO;

        for (Map.Entry<Long, BigDecimal> entry : requestVector.entrySet()) {
            BigDecimal specialistWeight = specialistVector.get(entry.getKey());
            if (specialistWeight != null) {
                dotProduct = dotProduct.add(entry.getValue().multiply(specialistWeight));
            }
        }

        if (dotProduct.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal requestNorm = sqrt(
                requestVector.values().stream()
                        .map(w -> w.multiply(w))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        BigDecimal specialistNorm = sqrt(
                specialistVector.values().stream()
                        .map(w -> w.multiply(w))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        if (requestNorm.compareTo(BigDecimal.ZERO) == 0
                || specialistNorm.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return dotProduct.divide(requestNorm.multiply(specialistNorm), MC);
    }

    private BigDecimal sqrt(BigDecimal value) {
        return BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
    }
}