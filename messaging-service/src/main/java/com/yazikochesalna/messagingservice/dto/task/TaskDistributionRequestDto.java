package com.yazikochesalna.messagingservice.dto.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionRequestDto {
    @NotNull
    private Long requestId;
    @NotNull
    private Long initiatorId;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal payment;
    @NotBlank
    private String specializationName;
    @NotEmpty
    @Valid
    private List<TaskDistributionRecipientDto> recipients;
}
