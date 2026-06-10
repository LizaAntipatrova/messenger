package com.yazikochesalna.messagingservice.service;

import com.yazikochesalna.messagingservice.dto.events.EventDTO;
import com.yazikochesalna.messagingservice.dto.events.EventType;
import com.yazikochesalna.messagingservice.dto.events.payload.chat.impl.ChatTaskRequestPayloadDTO;
import com.yazikochesalna.messagingservice.dto.task.TaskDistributionRecipientDto;
import com.yazikochesalna.messagingservice.dto.task.TaskDistributionRequestDto;
import com.yazikochesalna.messagingservice.dto.task.TaskDistributionResultDto;
import com.yazikochesalna.messagingservice.dto.task.TaskRequestResponseNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDistributionService {

    public static final String RESPONSE_STATUS_SENT = "SENT";

    private final ChatServiceClient chatServiceClient;
    private final KafkaProducerService kafkaProducerService;

    public TaskDistributionResultDto distributeTaskRequest(TaskDistributionRequestDto request) {
        List<TaskDistributionResultDto.RecipientDeliveryResultDto> deliveries = new ArrayList<>();

        for (TaskDistributionRecipientDto recipient : request.getRecipients()) {
            try {
                Long chatId = chatServiceClient.createOrGetDialog(
                        request.getInitiatorId(),
                        recipient.getSpecialistExternalUserId());

                UUID messageId = UUID.randomUUID();
                EventDTO event = buildTaskRequestEvent(
                        messageId,
                        chatId,
                        request.getRequestId(),
                        request.getInitiatorId(),
                        request.getDescription(),
                        request.getPayment(),
                        request.getSpecializationName(),
                        RESPONSE_STATUS_SENT);

                kafkaProducerService.sendMessage(event);

                deliveries.add(TaskDistributionResultDto.RecipientDeliveryResultDto.builder()
                        .recipientId(recipient.getRecipientId())
                        .chatId(chatId)
                        .messageId(messageId)
                        .build());

                log.info("Задача requestId={} отправлена специалисту userId={} в чат chatId={}",
                        request.getRequestId(), recipient.getSpecialistExternalUserId(), chatId);
            } catch (Exception e) {
                log.error("Не удалось разослать задачу requestId={} специалисту userId={}: {}",
                        request.getRequestId(), recipient.getSpecialistExternalUserId(), e.getMessage());
            }
        }

        return TaskDistributionResultDto.builder()
                .deliveries(deliveries)
                .build();
    }

    public void notifyTaskRequestResponse(TaskRequestResponseNotificationDto notification) {
        UUID messageId = UUID.randomUUID();
        EventDTO event = buildTaskRequestEvent(
                messageId,
                notification.getChatId(),
                notification.getRequestId(),
                notification.getInitiatorId(),
                notification.getDescription(),
                notification.getPayment(),
                notification.getSpecializationName(),
                notification.getResponseStatus());

        kafkaProducerService.sendMessage(event);
        log.info("Обновление статуса задачи requestId={} отправлено в чат chatId={}, status={}",
                notification.getRequestId(), notification.getChatId(), notification.getResponseStatus());
    }

    private EventDTO buildTaskRequestEvent(
            UUID messageId,
            Long chatId,
            Long requestId,
            Long initiatorId,
            String description,
            java.math.BigDecimal payment,
            String specializationName,
            String responseStatus) {

        ChatTaskRequestPayloadDTO payload = new ChatTaskRequestPayloadDTO();
        payload.setChatId(chatId);
        payload.setRequestId(requestId);
        payload.setInitiatorId(initiatorId);
        payload.setDescription(description);
        payload.setPayment(payment);
        payload.setSpecializationName(specializationName);
        payload.setResponseStatus(responseStatus);

        return EventDTO.builder()
                .type(EventType.TASK_REQUEST)
                .messageId(messageId)
                .payload(payload)
                .build();
    }
}
