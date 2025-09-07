package com.yazikochesalna.userservice.controller.externalcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazikochesalna.common.filter.JwtFilter;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserRequestDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import com.yazikochesalna.userservice.service.externalservice.UpdateProfileDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.ServiceUnavailableException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(
        controllers = UpdateProfileDataController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
class UpdateProfileDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UpdateProfileDataService updateProfileDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateUser_Success() throws Exception {
        Long userId = 1L;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newusername");
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");

        UpdateUserResponseDto responseDto = new UpdateUserResponseDto();
        responseDto.setUserId(userId);
        responseDto.setUsername("newusername");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");

        when(updateProfileDataService.updateUserProfile(eq(userId), any(UpdateUserRequestDto.class)))
                .thenReturn(responseDto);

        doNothing().when(updateProfileDataService).SendUsernameNotification(eq(userId), any(UpdateUserRequestDto.class));

        mockMvc.perform(patch("/api/v1/users/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value("newusername"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateUser_NotificationServiceUnavailable_Returns500() throws Exception {
        Long userId = 1L;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("newusername");

        when(updateProfileDataService.updateUserProfile(eq(userId), any(UpdateUserRequestDto.class)))
                .thenReturn(new UpdateUserResponseDto());

        doThrow(new ServiceUnavailableException("Messaging service is not available"))
                .when(updateProfileDataService).SendUsernameNotification(eq(userId), any(UpdateUserRequestDto.class));

        mockMvc.perform(patch("/api/v1/users/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }
}
