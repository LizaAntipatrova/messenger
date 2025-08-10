package com.yazikochesalna.userservice.dto.fileupdatedto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileUpdateRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private UUID fileUuid;
}
