package com.yazikochesalna.userservice.controller.externalcontroller;

import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserRequestDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import com.yazikochesalna.userservice.service.externalservice.MessagingClientService;
import com.yazikochesalna.userservice.service.externalservice.UpdateProfileDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API")
public class UpdateProfileDataController {

   private final UpdateProfileDataService updateProfileDataService;
//   private final MessagingClientService messagingClientService;

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDto updateDto
    ) throws ServiceUnavailableException {

        UpdateUserResponseDto response = updateProfileDataService.updateUserProfile(id, updateDto);

        updateProfileDataService.SendUsernameNotification(id, updateDto);

        return ResponseEntity.ok(response);
    }
}
