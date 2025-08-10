package com.yazikochesalna.userservice.controller.internalcontroller;

import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.dto.checkdto.CheckUsersRequestDto;
import com.yazikochesalna.userservice.dto.checkdto.CheckUsersResponseDto;
import com.yazikochesalna.userservice.dto.createuserdto.CreateUserRequestDto;
import com.yazikochesalna.userservice.dto.createuserdto.CreateUserResponseDto;
import com.yazikochesalna.userservice.service.internalservice.AuthUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Internal user API for auth", description = "Внутренние методы управления пользователями для сервиса авторизации")
public class AuthUserController {

    private final AuthUserService authUserService;

    @PostMapping("/check")
    @RolesAllowed("SERVICE")
    @Hidden
    public CheckUsersResponseDto checkUsersExistence(@RequestBody CheckUsersRequestDto checkUsersRequest) {
        List<Long> existingUsers = authUserService.findUsersIdsByIds(checkUsersRequest.usersIds());
        return new CheckUsersResponseDto(existingUsers);
    }

    @PostMapping
    @RolesAllowed("SERVICE")
    @Hidden
    public ResponseEntity<CreateUserResponseDto> createUser(
            @RequestBody CreateUserRequestDto request) {

        Users newUser = authUserService.createUser(request.getUsername());

        CreateUserResponseDto createUserResponseDTO = new CreateUserResponseDto(newUser.getId());
        return ResponseEntity.ok(createUserResponseDTO);
    }
}
