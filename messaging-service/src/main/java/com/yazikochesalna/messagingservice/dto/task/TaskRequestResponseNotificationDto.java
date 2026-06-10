package com.yazikochesalna.messagingservice.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskRequestResponseNotificationDto {
    @NotNull
    private Long requestId;
    @NotNull
    private Long chatId;
    @NotNull
    private Long initiatorId;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal payment;
    @NotBlank
    private String specializationName;
    @NotBlank
    private String responseStatus;
}
