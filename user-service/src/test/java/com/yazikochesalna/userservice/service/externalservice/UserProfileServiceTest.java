package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.dto.UserProfileDto;
import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.service.mapper.PersonalProfileMapper;
import com.yazikochesalna.userservice.service.mapper.UserProfileDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO:
//  оставить private методы после теста, где они вызываются или отнести в конец?

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private AuthorizationClientService authorizationClientService;
    @Mock
    private PersonalProfileMapper personalProfileMapper;
    @Mock
    private UserProfileDtoMapper userProfileDtoMapper;

    @InjectMocks
    private UserProfileService userProfileService;

    private Users testUser;
    private final Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setMiddleName("A.");
        testUser.setPhone("1234567890");
        testUser.setDescription("A test user.");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setFileUuid(UUID.randomUUID());
    }

    @Test
    void findUserProfile_UserFound_ShouldReturnUserProfileDto() {
        // Arrange
        UserProfileDto expectedDto = getExpectedUserProfileDto();

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userProfileDtoMapper.toUserProfileDto(testUser)).thenReturn(expectedDto);

        // Act
        UserProfileDto result = userProfileService.findUserProfile(testUserId);

        // Assert
        assertNotNull(result);
        assertUserProfileDtoResult(expectedDto, result);

        verify(usersRepository, times(1)).findById(testUserId);
        verify(userProfileDtoMapper, times(1)).toUserProfileDto(testUser);
    }

    private UserProfileDto getExpectedUserProfileDto() {
        UserProfileDto expectedDto = new UserProfileDto();
        expectedDto.setUsername(testUser.getUsername());
        expectedDto.setFirstName(testUser.getFirstName());
        expectedDto.setLastName(testUser.getLastName());
        expectedDto.setMiddleName(testUser.getMiddleName());
        expectedDto.setPhone(testUser.getPhone());
        expectedDto.setDescription(testUser.getDescription());
        expectedDto.setBirthDate(testUser.getBirthDate());
        expectedDto.setFileUuid(testUser.getFileUuid());
        return expectedDto;
    }

    private void assertUserProfileDtoResult(UserProfileDto expectedDto, UserProfileDto result) {
        assertEquals(expectedDto.getUsername(), result.getUsername());
        assertEquals(expectedDto.getUsername(), result.getUsername());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        assertEquals(expectedDto.getLastName(), result.getLastName());
        assertEquals(expectedDto.getMiddleName(), result.getMiddleName());
        assertEquals(expectedDto.getPhone(), result.getPhone());
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getBirthDate(), result.getBirthDate());
        assertEquals(expectedDto.getFileUuid(), result.getFileUuid());
    }

    @Test
    void findUserProfile_UserNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(usersRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundCustomException.class, () -> userProfileService.findUserProfile(testUserId));

        verify(usersRepository, times(1)).findById(testUserId);
        verify(userProfileDtoMapper, never()).toUserProfileDto(any());
    }

    @Test
    void findPersonalProfileDto_Success_ShouldReturnPersonalProfileDto() throws ServiceUnavailableException {
        // Arrange
        PersonalProfileDto expectedDto = getExpectedPersonalProfileDto();
        String testLogin = "testlogin";

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(personalProfileMapper.toPersonalProfileDto(testUser)).thenReturn(expectedDto);
        when(authorizationClientService.getUserLogin(testUserId)).thenReturn(testLogin);

        // Act
        PersonalProfileDto result = userProfileService.findPersonalProfileDto(testUserId);

        // Assert
        assertNotNull(result);
        assertPersonalProfileDtoResult(expectedDto, result, testLogin);

        verify(usersRepository, times(1)).findById(testUserId);
        verify(personalProfileMapper, times(1)).toPersonalProfileDto(testUser);
        verify(authorizationClientService, times(1)).getUserLogin(testUserId);
    }

    private PersonalProfileDto getExpectedPersonalProfileDto() {
        PersonalProfileDto expectedDto = new PersonalProfileDto();
        expectedDto.setUserId(testUserId);
        expectedDto.setUsername(testUser.getUsername());
        expectedDto.setFirstName(testUser.getFirstName());
        expectedDto.setLastName(testUser.getLastName());
        expectedDto.setMiddleName(testUser.getMiddleName());
        expectedDto.setPhone(testUser.getPhone());
        expectedDto.setDescription(testUser.getDescription());
        expectedDto.setBirthDate(testUser.getBirthDate());
        expectedDto.setFileUuid(testUser.getFileUuid());
        return expectedDto;
    }

    private void assertPersonalProfileDtoResult(PersonalProfileDto expectedDto, PersonalProfileDto result, String testLogin) {
        assertEquals(expectedDto.getUserId(), result.getUserId());
        assertEquals(expectedDto.getUsername(), result.getUsername());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        assertEquals(expectedDto.getLastName(), result.getLastName());
        assertEquals(expectedDto.getMiddleName(), result.getMiddleName());
        assertEquals(expectedDto.getPhone(), result.getPhone());
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getBirthDate(), result.getBirthDate());
        assertEquals(expectedDto.getFileUuid(), result.getFileUuid());
        assertEquals(testLogin, result.getLogin());
    }

    @Test
    void findPersonalProfileDto_UserNotFound_ShouldThrowResourceNotFoundException() throws ServiceUnavailableException {
        // Arrange
        when(usersRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundCustomException.class, () -> userProfileService.findPersonalProfileDto(testUserId));

        verify(usersRepository, times(1)).findById(testUserId);

        verify(personalProfileMapper, never()).toPersonalProfileDto(any());
        verify(authorizationClientService, never()).getUserLogin(anyLong());
    }

    @Test
    void findPersonalProfileDto_ServiceUnavailable_ShouldThrowException() throws ServiceUnavailableException {
        // Arrange
        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(authorizationClientService.getUserLogin(testUserId)).thenThrow(new ServiceUnavailableException());

        // Act & Assert
        assertThrows(ServiceUnavailableException.class, () -> userProfileService.findPersonalProfileDto(testUserId));

        verify(usersRepository, times(1)).findById(testUserId);
        verify(authorizationClientService, times(1)).getUserLogin(testUserId);

        verify(personalProfileMapper, never()).toPersonalProfileDto(any());
    }
}