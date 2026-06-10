package org.strongcat.taskservice.service;

import com.yazikochesalna.common.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.strongcat.taskservice.dto.internal.TaskDistributionRequestDto;
import org.strongcat.taskservice.dto.internal.TaskDistributionResultDto;
import org.strongcat.taskservice.dto.internal.TaskRequestResponseNotificationDto;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;

@Service
public class MessagingClientService {

    private static final String TASK_DISTRIBUTION_URL = "/api/v1/ws/notification/task-request-distribution";
    private static final String TASK_REQUEST_RESPONSE_URL = "/api/v1/ws/notification/task-request-response";

    private final JwtService jwtService;
    private final WebClient messagingServiceWebClient;

    @Value("${webclient.timeout:5}")
    private Long webClientTimeout;

    public MessagingClientService(
            JwtService jwtService,
            @Qualifier("messagingServiceWebClient") WebClient messagingServiceWebClient) {
        this.jwtService = jwtService;
        this.messagingServiceWebClient = messagingServiceWebClient;
    }

    public TaskDistributionResultDto distributeTaskRequest(TaskDistributionRequestDto request)
            throws ServiceUnavailableException {
        return messagingServiceWebClient.post()
                .uri(TASK_DISTRIBUTION_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generateServiceToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TaskDistributionResultDto.class)
                .timeout(Duration.ofSeconds(webClientTimeout))
                .blockOptional(Duration.ofSeconds(webClientTimeout))
                .orElseThrow(() -> new ServiceUnavailableException(
                        "Messaging Service is unavailable or failed to distribute task request"));
    }

    public void notifyTaskRequestResponse(TaskRequestResponseNotificationDto notification)
            throws ServiceUnavailableException {
        messagingServiceWebClient.post()
                .uri(TASK_REQUEST_RESPONSE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generateServiceToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(notification)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(webClientTimeout))
                .blockOptional(Duration.ofSeconds(webClientTimeout))
                .orElseThrow(() -> new ServiceUnavailableException(
                        "Messaging Service is unavailable or failed to notify task request response"));
    }
}
