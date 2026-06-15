package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.Request;
import org.strongcat.taskservice.data.entity.RequestRecipient;
import org.strongcat.taskservice.data.entity.RequestStatus;
import org.strongcat.taskservice.data.entity.ResponseStatus;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.enums.ResponseStatusName;
import org.strongcat.taskservice.data.repository.RequestRecipientRepository;
import org.strongcat.taskservice.data.repository.RequestRepository;
import org.strongcat.taskservice.data.repository.RequestStatusRepository;
import org.strongcat.taskservice.data.repository.ResponseStatusRepository;
import org.strongcat.taskservice.dto.SpecialistDecisionDto;
import org.strongcat.taskservice.dto.internal.TaskRequestResponseNotificationDto;
import org.strongcat.taskservice.validator.SpecialistDecisionValidator;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestResponseService {

    private final RequestRecipientRepository requestRecipientRepository;
    private final ResponseStatusRepository responseStatusRepository;
    private final RequestRepository requestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final MessagingClientService messagingClientService;
    private final SpecialistDecisionValidator specialistDecisionValidator;

    @Transactional
    public void acceptRequest(Long requestId, SpecialistDecisionDto specialistDecisionDto) {
        specialistDecisionValidator.validate(specialistDecisionDto);
        RequestRecipient recipient = changeResponseStatus(requestId,
                specialistDecisionDto.getSpecialistExternalUserId(),
                ResponseStatusName.ACCEPTED);
        notifyTaskResponse(recipient, ResponseStatusName.ACCEPTED);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена с id: " + requestId));
        RequestStatus analyzingStatus = requestStatusRepository.findByName(RequestStatusName.ACCEPTED.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус 'ACCEPTED' не инициализирован в справочнике БД"));
        request.setRequestStatus(analyzingStatus);
        requestRepository.save(request);

    }

    @Transactional
    public void rejectRequest(Long requestId, SpecialistDecisionDto specialistDecisionDto) {
        specialistDecisionValidator.validate(specialistDecisionDto);
        RequestRecipient recipient = changeResponseStatus(requestId,
                specialistDecisionDto.getSpecialistExternalUserId(),
                ResponseStatusName.DECLINED);
        notifyTaskResponse(recipient, ResponseStatusName.DECLINED);
    }

    @Transactional
    public void completeRequest(Long requestId, SpecialistDecisionDto specialistDecisionDto) {
        specialistDecisionValidator.validate(specialistDecisionDto);
        Request request = requestRepository.findAcceptedRequestBySpecialist(requestId,
                        specialistDecisionDto.getSpecialistExternalUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Не удалось завершить задачу. Задача с id " + requestId +
                                " не найдена, либо вы не являетесь её утвержденным исполнителем."
                ));

        RequestStatus completedStatus = requestStatusRepository.findByName(RequestStatusName.COMPLETED.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус 'COMPLETED' не инициализирован в БД"));

        request.setRequestStatus(completedStatus);
        requestRepository.save(request);
    }

    private RequestRecipient changeResponseStatus(Long requestId, Long specialistExternalUserId,
                                                  ResponseStatusName statusName) {
        RequestRecipient recipient = requestRecipientRepository
                .findByRequestIdAndSpecialistExternalUserId(requestId, specialistExternalUserId)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Адресованный запрос не найден для задачи с id: " + requestId + " и специалиста: " +
                                specialistExternalUserId
                ));

        ResponseStatus responseStatus = responseStatusRepository.findByName(statusName.getDatabaseName())
                .orElseThrow(() -> new IllegalStateException("Статус '" + statusName.getDatabaseName() +
                        "' не инициализирован в БД"));

        recipient.setResponseStatus(responseStatus);
        recipient.setRespondedAt(LocalDateTime.now());

        return requestRecipientRepository.save(recipient);
    }

    private void notifyTaskResponse(RequestRecipient recipient, ResponseStatusName statusName) {
        if (recipient.getExternalChatId() == null) {
            log.warn("Пропуск уведомления о статусе задачи: external_chat_id не задан для recipientId={}",
                    recipient.getId());
            return;
        }

        Request request = recipient.getRequest();
        TaskRequestResponseNotificationDto notification = TaskRequestResponseNotificationDto.builder()
                .requestId(request.getId())
                .chatId(recipient.getExternalChatId())
                .initiatorId(request.getInitiatorId())
                .description(request.getDescription())
                .payment(request.getPayment())
                .specializationName(request.getSpecialization().getName())
                .responseStatus(statusName.name())
                .build();

        try {
            messagingClientService.notifyTaskRequestResponse(notification);
        } catch (ServiceUnavailableException e) {
            log.error("Не удалось отправить уведомление о статусе задачи requestId={}: {}",
                    request.getId(), e.getMessage());
        }
    }
}
