package org.strongcat.taskservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateRequestDto {
    private Long initiatorId;
    private Long specializationId;
    private String description;
    private Integer requiredExperience;
    private BigDecimal payment;
    private Long expectedDurationDays;
    private List<Long> skillIds;
}