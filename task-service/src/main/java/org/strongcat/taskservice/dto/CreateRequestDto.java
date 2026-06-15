package org.strongcat.taskservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
/**
 * DTO для создания новой заявки.
 *
 * Валидация этого объекта выполняется вручную в классе CreateRequestValidator.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateRequestDto {
    /**
     * Идентификатор требуемой специализации.
     */
    private Long specializationId;
    /**
     * Текстовое описание задачи.
     */
    private String description;

    private Integer requiredExperience;
    /**
     * Предлагаемая оплата за выполнение задачи.
     */
    private BigDecimal payment;
    /**
     * Ожидаемая длительность выполнения задачи в днях.
     */
    private Long expectedDurationDays;
    /**
     * Навыки, явно указанные пользователем.
     */
    private List<Long> skillIds;
}

