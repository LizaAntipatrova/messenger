package org.strongcat.taskservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.strongcat.taskservice.dto.RegisterSpecialistDto;
import org.strongcat.taskservice.service.SpecialistService;

@RestController
@RequestMapping("/api/v1/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

    @PostMapping
    public ResponseEntity<Long> registerSpecialist(@RequestBody RegisterSpecialistDto dto) {
        Long specialistId = specialistService.registerOrUpdateSpecialist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(specialistId);
    }
}