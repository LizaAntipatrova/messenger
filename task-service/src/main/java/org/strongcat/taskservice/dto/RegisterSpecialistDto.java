package org.strongcat.taskservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterSpecialistDto {
    private Long externalUserId;
    private String description;
    private String specializationName;
    private Integer experienceMonths;
    private List<Long> skillIds;
    private BigDecimal minPayment;
    private BigDecimal avgTaskCost;
    private Integer preferredDifficultyMonths;
    private LocalDateTime lastActivityAt;
}