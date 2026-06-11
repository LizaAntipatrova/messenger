package org.strongcat.vitrinaservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.strongcat.vitrinaservice.dto.UserAnalyticsDto;
import org.strongcat.vitrinaservice.service.UserAnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class UserAnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    @GetMapping("/user/{idNaturalUser}")
    public ResponseEntity<UserAnalyticsDto> getUserAnalytics(@PathVariable Integer idNaturalUser) {
        return userAnalyticsService.getUserAnalytics(idNaturalUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}