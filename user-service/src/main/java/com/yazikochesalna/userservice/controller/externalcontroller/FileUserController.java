package com.yazikochesalna.userservice.controller.externalcontroller;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.service.externalservice.FileUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Internal file user API", description = "Внутренние методы управления пользователями для файл сервиса")
public class FileUserController {

    private final FileUserService fileUserService;

    @PatchMapping("/update-file")
    public ResponseEntity<Void> updateFileUuid(
            @RequestBody @Valid FileUpdateRequestDto requestDto) throws ServiceUnavailableException {

        fileUserService.updateUserFileUuidSendNotification(requestDto);

        return ResponseEntity.noContent().build();
    }
}
