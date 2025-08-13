package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.EventType;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserAvatarUpdatePayloadDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AvatarNotificationDtoMapperTest {

    @Test
    void convertFileUpdateRequestDtoToNotificationDto_shouldMapCorrectly() {
        // Arrange
        Long userId = 123L;
        UUID fileUuid = UUID.randomUUID();
        FileUpdateRequestDto requestDto = new FileUpdateRequestDto();
        requestDto.setUserId(userId);
        requestDto.setFileUuid(fileUuid);

        // Act
        NotificationDto result = AvatarNotificationDtoMapper.convertFileUpdateRequestDtoToNotificationDto(requestDto);
        UserAvatarUpdatePayloadDto payload = result.getPayload();

        // Assert
        assertNotNull(result);
        assertEquals(EventType.NEW_USER_AVATAR, result.getType());
        assertNotNull(payload);
        assertEquals(userId, payload.getUserId());
        assertEquals(fileUuid, payload.getAvatarId());
    }
}
