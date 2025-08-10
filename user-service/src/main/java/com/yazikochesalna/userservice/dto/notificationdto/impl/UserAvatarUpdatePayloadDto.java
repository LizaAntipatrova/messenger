package com.yazikochesalna.userservice.dto.notificationdto.impl;

import com.yazikochesalna.userservice.dto.notificationdto.UserPayloadDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserAvatarUpdatePayloadDto extends UserPayloadDto {
    @NotNull(message = "avatarId не может быть null")
    private UUID avatarId;
}
