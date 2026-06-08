package org.strongcat.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.strongcat.taskservice.service.RequestResponseService;
import org.strongcat.taskservice.service.RequestService;

@RestController
@RequestMapping("/api/v1/requests/{id}")
@RequiredArgsConstructor
public class RequestResponseController {

    private final RequestResponseService requestResponseService;
    private final RequestService requestService;

    @GetMapping("/request-status")
    public ResponseEntity<String> getRequestStatus(@PathVariable("id") Long requestId) {
        String status = requestService.getRequestStatus(requestId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/response-status/{user-id}")
    public ResponseEntity<String> getResponseStatus(
            @PathVariable("id") Long requestId,
            @PathVariable("id") Long userId) {

//        Long externalUserId = Long.parseLong(authentication.getName());
        String status = requestService.getResponseStatus(requestId, userId);
        return ResponseEntity.ok(status);
    }

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