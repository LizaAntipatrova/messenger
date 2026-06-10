package com.yazikochesalna.chatservice.dto;

import jakarta.validation.constraints.NotNull;

public record CreateDialogRequest(
        @NotNull Long userId,
        @NotNull Long partnerId
) {
}
