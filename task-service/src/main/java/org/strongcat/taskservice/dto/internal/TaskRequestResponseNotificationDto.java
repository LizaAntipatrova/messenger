package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskRequestResponseNotificationDto {
    private Long requestId;
    private Long chatId;
    private Long initiatorId;
    private String description;
    private BigDecimal payment;
    private String specializationName;
    private String responseStatus;
}
