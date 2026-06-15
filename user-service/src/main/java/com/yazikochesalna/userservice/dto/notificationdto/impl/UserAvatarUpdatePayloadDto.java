package com.yazikochesalna.userservice.dto.notificationdto.impl;

import com.yazikochesalna.userservice.dto.notificationdto.UserPayloadDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAvatarUpdatePayloadDto extends UserPayloadDto {
    @NotNull(message = "avatarId не может быть null")
    private UUID avatarId;
}
