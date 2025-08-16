package com.yazikochesalna.userservice.controller.externalcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazikochesalna.common.authentication.JwtAuthenticationToken;
import com.yazikochesalna.common.filter.JwtFilter;
import com.yazikochesalna.userservice.dto.UserProfileDto;
import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.service.externalservice.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.ServiceUnavailableException;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(
        controllers = UserProfileController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserProfile_Success_ShouldReturnUserProfileDto() throws Exception {
        // Arrange
        Long testUserId = 1L;
        UserProfileDto expectedDto = getExpectedUserProfileDto();

        when(userProfileService.findUserProfile(testUserId)).thenReturn(expectedDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(expectedDto.getUsername()))
                .andExpect(jsonPath("$.firstName").value(expectedDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedDto.getLastName()))
                .andExpect(jsonPath("$.middleName").value(expectedDto.getMiddleName()))
                .andExpect(jsonPath("$.phone").value(expectedDto.getPhone()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.birthDate").value(expectedDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.fileUuid").value(expectedDto.getFileUuid().toString()));

        verify(userProfileService, times(1)).findUserProfile(testUserId);
    }

    private UserProfileDto getExpectedUserProfileDto() {
        UserProfileDto expectedDto = new UserProfileDto();
        expectedDto.setUsername("testuser");
        expectedDto.setFirstName("John");
        expectedDto.setLastName("Doe");
        expectedDto.setMiddleName("A.");
        expectedDto.setPhone("123-456-7890");
        expectedDto.setDescription("This is a test user.");
        expectedDto.setBirthDate(LocalDate.of(1990, 1, 1));
        expectedDto.setFileUuid(UUID.fromString("c0c32600-4f51-4d3f-b847-16781f3d8e57"));
        return expectedDto;
    }

    @Test
    void getUserProfile_UserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        // Не существующий id
        Long testUserId = 99L;
        when(userProfileService.findUserProfile(testUserId)).thenThrow(
                new ResourceNotFoundCustomException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userProfileService, times(1)).findUserProfile(testUserId);
    }

    @Test
    void getPersonalProfile_Success_ShouldReturnPersonalProfileDto() throws Exception {
        // Arrange
        Long authenticatedUserId = 101L;

        JwtAuthenticationToken mockAuthentication = mock(JwtAuthenticationToken.class);
        when(mockAuthentication.getUserId()).thenReturn(authenticatedUserId);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        PersonalProfileDto expectedDto = new PersonalProfileDto();
        expectedDto.setUserId(authenticatedUserId);
        expectedDto.setUsername("authuser");
        expectedDto.setLogin("authuser_login");
        expectedDto.setFirstName("John");
        expectedDto.setLastName("Doe");
        expectedDto.setMiddleName("A.");
        expectedDto.setPhone("123-456-7890");
        expectedDto.setDescription("This is a test user.");
        expectedDto.setBirthDate(LocalDate.of(1990, 1, 1));
        expectedDto.setFileUuid(UUID.fromString("c0c32600-4f51-4d3f-b847-16781f3d8e57"));

        when(userProfileService.findPersonalProfileDto(authenticatedUserId)).
                thenReturn(expectedDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(authenticatedUserId))
                .andExpect(jsonPath("$.username").value(expectedDto.getUsername()))
                .andExpect(jsonPath("$.login").value(expectedDto.getLogin()))
                .andExpect(jsonPath("$.firstName").value(expectedDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedDto.getLastName()))
                .andExpect(jsonPath("$.middleName").value(expectedDto.getMiddleName()))
                .andExpect(jsonPath("$.phone").value(expectedDto.getPhone()))
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.birthDate").value(expectedDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.fileUuid").value(expectedDto.getFileUuid().toString()));


        verify(userProfileService, times(1)).
                findPersonalProfileDto(authenticatedUserId);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getPersonalProfile_ServiceUnavailable_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        Long authenticatedUserId = 101L;

        JwtAuthenticationToken mockAuthentication = mock(JwtAuthenticationToken.class);
        when(mockAuthentication.getUserId()).thenReturn(authenticatedUserId);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userProfileService.findPersonalProfileDto(authenticatedUserId)).thenThrow(
                new ServiceUnavailableException());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userProfileService, times(1)).
                findPersonalProfileDto(authenticatedUserId);
        SecurityContextHolder.clearContext();
    }
}