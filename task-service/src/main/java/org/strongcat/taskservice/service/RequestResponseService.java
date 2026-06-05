package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.Request;
import org.strongcat.taskservice.data.entity.RequestRecipient;
import org.strongcat.taskservice.data.entity.RequestStatus;
import org.strongcat.taskservice.data.entity.ResponseStatus;
//import org.strongcat.taskservice.data.entity.ResponseStatusName;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.enums.ResponseStatusName;
import org.strongcat.taskservice.data.repository.RequestRecipientRepository;
import org.strongcat.taskservice.data.repository.RequestRepository;
import org.strongcat.taskservice.data.repository.RequestStatusRepository;
import org.strongcat.taskservice.data.repository.ResponseStatusRepository;
//import org.strongcat.taskservice.dto.event.SystemMessageEvent;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestResponseService {

    private final RequestRecipientRepository requestRecipientRepository;
    private final ResponseStatusRepository responseStatusRepository;
    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    // Предполагаем, что у тебя будет компонент отправки в Kafka, например:
    // private final KafkaTemplate<String, SystemMessageEvent> kafkaTemplate;

    @Transactional
    public void acceptRequest(Long requestId, Long specialistExternalUserId) {
        changeResponseStatus(requestId, specialistExternalUserId, ResponseStatusName.ACCEPTED);
        
        // Тут можно вызвать отправку в Kafka:
        // publishSystemMessage(recipient, "Специалист принял вашу задачу!");
    }

    @Transactional
    public void rejectRequest(Long requestId, Long specialistExternalUserId) {
        changeResponseStatus(requestId, specialistExternalUserId, ResponseStatusName.DECLINED);
        
        // Тут можно вызвать отправку в Kafka:
        // publishSystemMessage(recipient, "Специалист отклонил вашу задачу.");
    }

    // Добавь этот метод в твой существующий RequestResponseService

    @Transactional
    public void completeRequest(Long requestId, Long specialistExternalUserId) {
        // 1. Проверяем, что задача существует и была принята именно этим специалистом
        Request request = requestRepository.findAcceptedRequestBySpecialist(requestId, specialistExternalUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Не удалось завершить задачу. Задача с id " + requestId +
                                " не найдена, либо вы не являетесь её утвержденным исполнителем."
                ));

        // 2. Находим статус COMPLETED в справочнике через наш Enum
        RequestStatus completedStatus = requestStatusRepository.findByName(RequestStatusName.COMPLETED.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус 'COMPLETED' не инициализирован в БД"));

        // 3. Обновляем статус задачи
        request.setRequestStatus(completedStatus);
        requestRepository.save(request);

        // 4. Отправляем системное уведомление в Kafka для инициатора задачи
        // String messageText = "Исполнитель отметил задачу '" + request.getDescription() + "' как выполненную!";
        // publishSystemMessage(request.getInitiatorId(), specialistExternalUserId, messageText);
    }

    private RequestRecipient changeResponseStatus(Long requestId, Long specialistExternalUserId,
                                                  ResponseStatusName statusName) {
        // 1. Находим запись адресации
        RequestRecipient recipient = requestRecipientRepository
                .findByRequestIdAndSpecialistExternalUserId(requestId, specialistExternalUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Адресованный запрос не найден для задачи с id: " + requestId + " и специалиста: " +
                                specialistExternalUserId
                ));

        // 2. Находим целевой статус в БД через наш Enum
        ResponseStatus responseStatus = responseStatusRepository.findByName(statusName.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус '" + statusName.getDatabaseName() +
                        "' не инициализирован в БД"));

        // 3. Обновляем данные
        recipient.setResponseStatus(responseStatus);
        recipient.setRespondedAt(LocalDateTime.now());

        return requestRecipientRepository.save(recipient);
    }
}