package com.yazikochesalna.messagingservice.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionResultDto {
    private List<RecipientDeliveryResultDto> deliveries;

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecipientDeliveryResultDto {
        private Long recipientId;
        private Long chatId;
        private UUID messageId;
    }
}
