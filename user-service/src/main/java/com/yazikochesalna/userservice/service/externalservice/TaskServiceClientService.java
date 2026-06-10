package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.common.service.JwtService;
import com.yazikochesalna.userservice.dto.internal.RegisterSpecialistDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;

@Service
public class TaskServiceClientService {

    private static final String SYNC_SPECIALIST_URL = "/api/v1/specialists";

    private final JwtService jwtService;
    private final WebClient taskServiceWebClient;

    @Value("${webclient.timeout}")
    private Long webClientTimeout;

    public TaskServiceClientService(
            JwtService jwtService,
            @Qualifier("taskServiceWebClient") WebClient taskServiceWebClient) {
        this.jwtService = jwtService;
        this.taskServiceWebClient = taskServiceWebClient;
    }

    public void syncSpecialist(RegisterSpecialistDto specialistDto) throws ServiceUnavailableException {
        taskServiceWebClient.post()
                .uri(SYNC_SPECIALIST_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generateServiceToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(specialistDto)
                .retrieve()
                .bodyToMono(Long.class)
                .timeout(Duration.ofSeconds(webClientTimeout))
                .blockOptional(Duration.ofSeconds(webClientTimeout))
                .orElseThrow(() -> new ServiceUnavailableException(
                        "Task Service is unavailable or failed to process sync request"));
    }
}
