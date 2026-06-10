package com.yazikochesalna.messagingservice.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionRecipientDto {
    @NotNull
    private Long recipientId;
    @NotNull
    private Long specialistExternalUserId;
    private Integer rankPosition;
}
