package org.strongcat.taskservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestSkillDto {
    private Long skillId;
    private BigDecimal skillWeight;
}