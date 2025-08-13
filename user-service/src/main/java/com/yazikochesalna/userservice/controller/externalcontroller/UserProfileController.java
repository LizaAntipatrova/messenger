package com.yazikochesalna.userservice.controller.externalcontroller;

import com.yazikochesalna.common.authentication.JwtAuthenticationToken;
import com.yazikochesalna.userservice.dto.UserProfileDto;
import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
import com.yazikochesalna.userservice.service.externalservice.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Управление пользователями")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по id", description = "Возвращает userName пользователя")
    public  ResponseEntity<UserProfileDto> getUserProfile (@PathVariable Long userId){
        UserProfileDto profile = userProfileService.findUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/me")
    @Operation(summary = "Получить личный профиль по id из jwt", description = "Возвращает данные пользователя")
    public ResponseEntity<PersonalProfileDto> getPersonalProfile ()
            throws ServiceUnavailableException{

        Long userId = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        PersonalProfileDto profileDto = userProfileService.findPersonalProfileDto(userId);

        return ResponseEntity.ok(profileDto);
    }
}
