package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionResultDto {
    private List<RecipientDeliveryResultDto> deliveries;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecipientDeliveryResultDto {
        private Long recipientId;
        private Long chatId;
        private UUID messageId;
    }
}
