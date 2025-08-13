package com.yazikochesalna.userservice.controller.externalcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazikochesalna.common.authentication.JwtAuthenticationToken;
import com.yazikochesalna.common.filter.JwtFilter;
import com.yazikochesalna.common.service.JwtService;
import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.service.externalservice.FileUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import javax.naming.ServiceUnavailableException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(FileUserController.class)
@WebMvcTest(
        controllers = FileUserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
class FileUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(FileUserService.class)
    private FileUserService fileUserService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void updateFileUuid_success_shouldReturnNoContent() throws Exception {
        // Arrange
        Long userId = 1L;
        FileUpdateRequestDto requestDto = new FileUpdateRequestDto();
        requestDto.setUserId(userId);
        requestDto.setFileUuid(UUID.randomUUID());

        doNothing().when(fileUserService).updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/update-file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());

        verify(fileUserService, times(1)).
                updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));
    }

    @Test
    void updateFileUuid_serviceUnavailable_shouldReturnServiceUnavailable() throws Exception {
        // Arrange
        FileUpdateRequestDto requestDto = new FileUpdateRequestDto();
        requestDto.setUserId(1L);
        requestDto.setFileUuid(UUID.randomUUID());

        doThrow(new ServiceUnavailableException("Messaging service is unavailable")).when(fileUserService)
                .updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/update-file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());

        verify(fileUserService, times(1)).
                updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));
    }

    @Test
    void updateFileUuid_invalidRequest_shouldReturnBadRequest() throws Exception {
        // Arrange
        FileUpdateRequestDto invalidRequestDto = new FileUpdateRequestDto();
        invalidRequestDto.setUserId(null);
        invalidRequestDto.setFileUuid(UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/update-file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());

        verify(fileUserService, never()).updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));
    }

}