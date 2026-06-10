package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionRequestDto {
    private Long requestId;
    private Long initiatorId;
    private String description;
    private BigDecimal payment;
    private String specializationName;
    private List<TaskDistributionRecipientDto> recipients;
}
