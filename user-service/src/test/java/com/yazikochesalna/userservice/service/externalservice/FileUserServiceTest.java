package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.ServiceUnavailableException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private MessagingClientService messagingClientService;

    @InjectMocks
    private FileUserService fileUserService;

    private FileUpdateRequestDto requestDto;
    private Users user;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        UUID newFileUuid = UUID.randomUUID();

        requestDto = new FileUpdateRequestDto();
        requestDto.setUserId(userId);
        requestDto.setFileUuid(newFileUuid);

        user = new Users();
        user.setId(userId);
        user.setFileUuid(UUID.randomUUID());
    }

    @Test
    void UpdateUserFileUuidSendNotification_ReturnOk() throws ServiceUnavailableException {
        // Arrange
        when(usersRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(user));

        // Act
        fileUserService.updateUserFileUuidSendNotification(requestDto);

        // Assert
        verify(usersRepository, times(1)).findById(requestDto.getUserId());
        verify(usersRepository, times(1)).updateFileUuid(requestDto.getUserId(), requestDto.getFileUuid());
        verify(messagingClientService, times(1)).setNewAvatar(any(NotificationDto.class));
    }


    @Test
    void updateUserFileUuidSendNotification_userNotFound_shouldThrowException() throws ServiceUnavailableException {
        // Arrange
        when(usersRepository.findById(requestDto.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundCustomException.class,
                () -> fileUserService.updateUserFileUuidSendNotification(requestDto));

        verify(usersRepository, never()).updateFileUuid(anyLong(), any(UUID.class));
        verify(messagingClientService, never()).setNewAvatar(any(NotificationDto.class));
    }

}