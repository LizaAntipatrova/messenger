package com.yazikochesalna.messagingservice.dto.events.payload.chat;

import com.yazikochesalna.messagingservice.dto.events.payload.PayloadDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPayloadDTO extends PayloadDTO {
    @NotNull(message = "chatId не может быть null")
    protected Long chatId;

}
