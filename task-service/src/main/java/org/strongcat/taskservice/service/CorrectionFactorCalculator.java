package org.strongcat.taskservice.service;

import org.springframework.stereotype.Component;
import org.strongcat.taskservice.data.entity.Specialist;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CorrectionFactorCalculator {

    private static final MathContext MC = new MathContext(5);

    public BigDecimal calculateCoefficient(Specialist specialist) {
        // По умолчанию коэффициент равен 1.0 (максимальный)
        BigDecimal coefficient = BigDecimal.ONE;

        if (specialist.getLastActivityAt() == null) {
            // Если активности никогда не было, даем базовый штраф
            return new BigDecimal("0.70", MC);
        }

        // Вычисляем, сколько дней назад специалист проявлял активность
        long daysSinceLastActivity = Duration.between(specialist.getLastActivityAt(), LocalDateTime.now()).toDays();

        if (daysSinceLastActivity > 30) {
            // Если не заходил больше месяца — штрафуем сильнее
            coefficient = new BigDecimal("0.75", MC);
        } else if (daysSinceLastActivity > 7) {
            // Если не заходил больше недели — легкий штраф
            coefficient = new BigDecimal("0.90", MC);
        }

        // Сюда же при необходимости можно добавить бизнес-логику для:
        // - s.getAvgTaskCost() (сопоставление со средней стоимостью)
        // - s.getPreferredDifficultyMonths() (сопоставление со сложностью)

        return coefficient;
    }
}