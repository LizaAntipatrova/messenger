package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.dto.notificationdto.EventType;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserUsernameUpdatePayloadDto;

public class UsernameNotificationDtoMapper {

    public static NotificationDto convertUpdateUserRequestDtoToNotificationDto
            (Long id, String username){
        UserUsernameUpdatePayloadDto payload = new UserUsernameUpdatePayloadDto();
        payload.setUserId(id);
        payload.setUsername(username);

        NotificationDto notification = new NotificationDto();
        notification.setType(EventType.NEW_USERNAME);
        notification.setPayload(payload);

        return notification;
    }
}
