package org.strongcat.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.strongcat.taskservice.dto.CreateRequestDto;
import org.strongcat.taskservice.service.RequestService;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<Long> createRequest(@RequestBody CreateRequestDto dto, Authentication authentication) {
        Long requestId = requestService.createRequest(dto, Long.parseLong(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
    }

    @GetMapping("/{requestId}/accepted-specialist")
    public ResponseEntity<Long> getAcceptedSpecialist(@PathVariable("requestId") Long requestId) {
        return requestService.getAcceptedSpecialistExternalUserId(requestId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }



}