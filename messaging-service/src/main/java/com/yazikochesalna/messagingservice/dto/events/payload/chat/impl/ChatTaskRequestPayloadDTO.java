package com.yazikochesalna.messagingservice.dto.events.payload.chat.impl;

import com.yazikochesalna.messagingservice.dto.events.payload.chat.ChatPayloadDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChatTaskRequestPayloadDTO extends ChatPayloadDTO {
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
    @NotBlank
    private String responseStatus;
}
