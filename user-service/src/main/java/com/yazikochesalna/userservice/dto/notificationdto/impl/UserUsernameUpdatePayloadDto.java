package com.yazikochesalna.userservice.dto.notificationdto.impl;

import com.yazikochesalna.userservice.dto.notificationdto.UserPayloadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserUsernameUpdatePayloadDto extends UserPayloadDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
}
