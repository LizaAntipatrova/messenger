package com.yazikochesalna.userservice.dto.checkdto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckUsersRequestDto(
        @NotNull(message = "userIds must not be null")
        List<Long> usersIds
) {
}
