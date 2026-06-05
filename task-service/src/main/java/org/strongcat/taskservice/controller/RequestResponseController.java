package org.strongcat.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.strongcat.taskservice.service.RequestResponseService;

@RestController
@RequestMapping("/api/v1/requests/{id}")
@RequiredArgsConstructor
public class RequestResponseController {

    private final RequestResponseService requestResponseService;

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable("id") Long requestId, Authentication authentication) {
        Long specialistExternalUserId = getExternalUserIdFromAuth(authentication);
        
        requestResponseService.acceptRequest(requestId, specialistExternalUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable("id") Long requestId, Authentication authentication) {
        Long specialistExternalUserId = getExternalUserIdFromAuth(authentication);
        
        requestResponseService.rejectRequest(requestId, specialistExternalUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeRequest(@PathVariable("id") Long requestId, Authentication authentication) {
        Long specialistExternalUserId = getExternalUserIdFromAuth(authentication);

        requestResponseService.completeRequest(requestId, specialistExternalUserId);
        return ResponseEntity.ok().build();
    }

    private Long getExternalUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}