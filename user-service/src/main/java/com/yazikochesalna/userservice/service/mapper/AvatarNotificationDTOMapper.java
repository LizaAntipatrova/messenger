package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.EventType;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserAvatarUpdatePayloadDto;

public class AvatarNotificationDTOMapper {

    public static NotificationDto convertFileUpdateRequestDTOToNotificationDTO
            (FileUpdateRequestDto requestDTO){
        UserAvatarUpdatePayloadDto payload = new UserAvatarUpdatePayloadDto();
        payload.setUserId(requestDTO.getUserId());
        payload.setAvatarId(requestDTO.getFileUuid());

        NotificationDto notification = new NotificationDto();
        notification.setType(EventType.NEW_USER_AVATAR);
        notification.setPayload(payload);

        return notification;
    }
}
