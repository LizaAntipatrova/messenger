package com.yazikochesalna.userservice.dto.notificationdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private EventType type;

    protected PayloadDto payload;

    public <T extends PayloadDto> T getPayload() {
        return (T) payload;
    }
}
