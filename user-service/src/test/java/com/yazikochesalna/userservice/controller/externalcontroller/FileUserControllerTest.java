package com.yazikochesalna.userservice.controller.externalcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazikochesalna.common.authentication.JwtAuthenticationToken;
import com.yazikochesalna.common.filter.JwtFilter;
import com.yazikochesalna.common.service.JwtService;
import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.service.externalservice.FileUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import javax.naming.ServiceUnavailableException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUserController.class)
class FileUserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private

    @MockBean
    private FileUserService fileUserService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private JwtFilter jwtFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
//    @WithMockUser
    void updateFileUuid_success_shouldReturnNoContent() throws Exception {
        // Arrange
        Long userId = 1L;

        JwtAuthenticationToken authToken = Mockito.mock(JwtAuthenticationToken.class);
        when(authToken.getUserId()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(authToken);

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
//    @WithMockUser
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
                .andExpect(status().isServiceUnavailable());

        verify(fileUserService, times(1)).updateUserFileUuidSendNotification(any(FileUpdateRequestDto.class));
    }

    @Test
//    @WithMockUser
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