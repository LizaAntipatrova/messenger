package com.yazikochesalna.messagingservice.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDialogRequestDto {
    private Long userId;
    private Long partnerId;
}
