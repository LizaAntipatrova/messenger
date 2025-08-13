package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.EventType;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserAvatarUpdatePayloadDto;
import com.yazikochesalna.userservice.dto.notificationdto.impl.UserUsernameUpdatePayloadDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsernameNotificationDtoMapperTest {

    @Test
    void convertUpdateUserRequestDtoToNotificationDto_shouldMapCorrectly(){
        // Arrange
        Long userId = 123L;
        String username = "test";

        // Act
        NotificationDto result = UsernameNotificationDtoMapper.
                convertUpdateUserRequestDtoToNotificationDto(userId, username);
        UserUsernameUpdatePayloadDto payload = result.getPayload();

        // Assert
        assertNotNull(result);
        assertEquals(EventType.NEW_USERNAME, result.getType());
        assertNotNull(payload);
        assertEquals(userId, payload.getUserId());
        assertEquals(username, payload.getUsername());
    }
}