package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.entity.Skill;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.SkillRepository;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserRequestDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.exception.UserAlreadyExistsCustomException;
import com.yazikochesalna.userservice.service.mapper.UploadUserMapper;
import com.yazikochesalna.userservice.service.mapper.UsernameNotificationDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileDataServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UploadUserMapper uploadUserMapper;
    @Mock
    private MessagingClientService messagingClientService;
    @Mock
    private TaskServiceClientService taskServiceClientService;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private UpdateProfileDataService updateProfileDataService;

    private Users testUser;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(testUserId);
        testUser.setUsername("Oldusername");
        testUser.setLow_username("oldusername");
    }

    @Test
    void sendUsernameNotification_UsernameProvided_ShouldSendMessage() throws ServiceUnavailableException {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setUsername("newusername");

        NotificationDto notification = new NotificationDto();

        try (var mockedStatic = mockStatic(UsernameNotificationDtoMapper.class)) {
            mockedStatic.when(() -> UsernameNotificationDtoMapper
                            .convertUpdateUserRequestDtoToNotificationDto(anyLong(), anyString()))
                            .thenReturn(notification);

            // Act
            updateProfileDataService.SendUsernameNotification(testUserId, updateDto);

            // Assert
            verify(messagingClientService, times(1)).setNewUsername(notification);
        }
    }

    @Test
    void sendUsernameNotification_UsernameIsNull_ShouldNotSendMessage() throws ServiceUnavailableException {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setUsername(null);

        // Act
        updateProfileDataService.SendUsernameNotification(testUserId, updateDto);

        // Assert
        verify(messagingClientService, never()).setNewUsername(any());
    }

    @Test
    void sendUsernameNotification_ServiceUnavailable_ShouldThrowException() throws ServiceUnavailableException {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setUsername("newusername");

        try (var mockedStatic = mockStatic(UsernameNotificationDtoMapper.class)) {
            mockedStatic.when(() -> UsernameNotificationDtoMapper.convertUpdateUserRequestDtoToNotificationDto(
                    anyLong(), anyString()))
                    .thenReturn(new NotificationDto());

            doThrow(new ServiceUnavailableException()).when(messagingClientService).setNewUsername(any());

            // Act & Assert
            assertThrows(ServiceUnavailableException.class,
                    () -> updateProfileDataService.SendUsernameNotification(testUserId, updateDto));
        }
    }


    // --- Тесты для метода updateUserProfile ---

    @Test
    void updateUserProfile_UserFound_ShouldUpdateAndReturnResponse() {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setUsername("newusername");
        updateDto.setFirstName("UpdatedName");
        updateDto.setLastName("UpdatedLastName");
        updateDto.setMiddleName("UpdatedMiddleName");
        updateDto.setPhone("1234567890");
        updateDto.setDescription("Updated description");
        updateDto.setBirthDate(LocalDate.of(2000, 1, 1));

        UpdateUserResponseDto expectedResponseDto = new UpdateUserResponseDto();

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);
        when(uploadUserMapper.toUpdateUserResponseDto(any(Users.class))).thenReturn(expectedResponseDto);
        when(usersRepository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(false);

        // Act
        UpdateUserResponseDto result = updateProfileDataService.updateUserProfile(testUserId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.getUsername(), testUser.getUsername());
        assertEquals(updateDto.getFirstName(), testUser.getFirstName());
        assertEquals(updateDto.getLastName(), testUser.getLastName());
        assertEquals(updateDto.getMiddleName(), testUser.getMiddleName());
        assertEquals(updateDto.getPhone(), testUser.getPhone());
        assertEquals(updateDto.getDescription(), testUser.getDescription());
        assertEquals(updateDto.getBirthDate(), testUser.getBirthDate());
        assertEquals(updateDto.getUsername().toLowerCase(), testUser.getLow_username());

        verify(usersRepository, times(1)).findById(testUserId);
        verify(usersRepository, times(1)).existsByUsernameAndIdNot(updateDto.getUsername(), testUserId);
        verify(usersRepository, times(1)).save(testUser);
        verify(uploadUserMapper, times(1)).toUpdateUserResponseDto(testUser);
    }

    @Test
    void updateUserProfile_UserNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        when(usersRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundCustomException.class,
                () -> updateProfileDataService.updateUserProfile(testUserId, updateDto));

        verify(usersRepository, times(1)).findById(testUserId);
        verify(usersRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_UsernameExists_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setUsername("existinguser");

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(usersRepository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsCustomException.class,
                () -> updateProfileDataService.updateUserProfile(testUserId, updateDto));

        verify(usersRepository, times(1)).findById(testUserId);
        verify(usersRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_NoChanges_ShouldNotUpdateUser() {
        // Arrange
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);
        when(uploadUserMapper.toUpdateUserResponseDto(any(Users.class))).thenReturn(new UpdateUserResponseDto());

        // Act
        UpdateUserResponseDto result = updateProfileDataService.updateUserProfile(testUserId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Oldusername", testUser.getUsername());
        assertEquals("oldusername", testUser.getUsername().toLowerCase());
        assertNull(testUser.getBirthDate());
        assertNull(testUser.getDescription());
        assertNull(testUser.getFirstName());
        assertNull(testUser.getLastName());
        assertNull(testUser.getMiddleName());
        assertNull(testUser.getPhone());

        verify(usersRepository, times(1)).findById(testUserId);
        verify(usersRepository, times(1)).save(testUser);
        verify(uploadUserMapper, times(1)).toUpdateUserResponseDto(testUser);
    }
}