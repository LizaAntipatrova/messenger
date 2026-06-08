package com.yazikochesalna.messagingservice.controller;

import com.yazikochesalna.messagingservice.dto.events.EventDTO;
import com.yazikochesalna.messagingservice.dto.task.TaskDistributionRequestDto;
import com.yazikochesalna.messagingservice.dto.task.TaskDistributionResultDto;
import com.yazikochesalna.messagingservice.dto.task.TaskRequestResponseNotificationDto;
import com.yazikochesalna.messagingservice.service.TaskDistributionService;
import com.yazikochesalna.messagingservice.service.WebSocketEventService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ws/notification")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final WebSocketEventService webSocketEventService;
    private final TaskDistributionService taskDistributionService;

    @PostMapping("/update-members")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Отправка уведомления об изменении состава участников ",
            description = "Внтуренний метод для отправки уведомлений о добавлении или удалении нового пользователя. Доступен только для сервисов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление принято в рассылку"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<?> sendUpdateChatMember(@Valid @RequestBody EventDTO eventDTO) {
        webSocketEventService.sendMessage(eventDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-chat-avatar")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Отправка уведомления о новой аватарки чата",
            description = "Внтуренний метод для отправки уведомлений об установки новой аватарки чата. Доступен только для сервисов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление принято в рассылку"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<?> sendNewChatAvatar(@Valid @RequestBody EventDTO eventDTO) {
        webSocketEventService.sendMessage(eventDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-user-avatar")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Отправка уведомления о новой аватарки пользователя",
            description = "Внтуренний метод для отправки уведомлений об установки новой аватарки пользователя. Доступен только для сервисов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление принято в рассылку"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<?> sendNewUserAvatar(@Valid @RequestBody EventDTO eventDTO) {
        webSocketEventService.sendEvent(eventDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-username")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Отправка уведомления о новом имени пользователя",
            description = "Внтуренний метод для отправки уведомлений о новом имени пользователя. Доступен только для сервисов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление принято в рассылку"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<?> sendNewUsername(@Valid @RequestBody EventDTO eventDTO) {
        webSocketEventService.sendEvent(eventDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task-request-distribution")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Рассылка задачи потенциальным исполнителям",
            description = "Создаёт личные чаты заказчик-исполнитель и публикует сообщения в Kafka.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Рассылка выполнена"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<TaskDistributionResultDto> distributeTaskRequest(
            @Valid @RequestBody TaskDistributionRequestDto request) {
        return ResponseEntity.ok(taskDistributionService.distributeTaskRequest(request));
    }

    @PostMapping("/task-request-response")
    @RolesAllowed("SERVICE")
    @Operation(summary = "Уведомление об ответе исполнителя на задачу",
            description = "Публикует TASK_REQUEST с обновлённым responseStatus (ACCEPTED/DECLINED) в Kafka.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление принято в рассылку"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат запроса")
    })
    @Hidden
    public ResponseEntity<Void> notifyTaskRequestResponse(
            @Valid @RequestBody TaskRequestResponseNotificationDto notification) {
        taskDistributionService.notifyTaskRequestResponse(notification);
        return ResponseEntity.ok().build();
    }


}
