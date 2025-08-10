package com.yazikochesalna.userservice.dto.createuserdto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotNull(message = "username can not be null")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;
}
