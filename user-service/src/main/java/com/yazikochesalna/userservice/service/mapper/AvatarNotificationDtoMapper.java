package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.EventType;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserAvatarUpdatePayloadDto;

public class AvatarNotificationDtoMapper {

    public static NotificationDto convertFileUpdateRequestDtoToNotificationDto
            (FileUpdateRequestDto requestDto){
        UserAvatarUpdatePayloadDto payload = new UserAvatarUpdatePayloadDto();
        payload.setUserId(requestDto.getUserId());
        payload.setAvatarId(requestDto.getFileUuid());

        NotificationDto notification = new NotificationDto();
        notification.setType(EventType.NEW_USER_AVATAR);
        notification.setPayload(payload);

        return notification;
    }
}
