package org.strongcat.taskservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.taskservice.data.entity.RequestRecipient;
import org.strongcat.taskservice.data.repository.RequestRecipientRepository;
import org.strongcat.taskservice.dto.internal.TaskDistributionRecipientDto;
import org.strongcat.taskservice.dto.internal.TaskDistributionRequestDto;
import org.strongcat.taskservice.dto.internal.TaskDistributionResultDto;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestDistributionService {

    private final MessagingClientService messagingClientService;
    private final RequestRecipientRepository requestRecipientRepository;

    @Transactional
    public void distributeRequestToSpecialists(
            Long requestId,
            Long initiatorId,
            String description,
            BigDecimal payment,
            String specializationName,
            List<TaskDistributionRecipientDto> recipients) {

        TaskDistributionRequestDto distributionRequest = TaskDistributionRequestDto.builder()
                .requestId(requestId)
                .initiatorId(initiatorId)
                .description(description)
                .payment(payment)
                .specializationName(specializationName)
                .recipients(recipients)
                .build();

        try {
            TaskDistributionResultDto result = messagingClientService.distributeTaskRequest(distributionRequest);
            updateRecipientDeliveryInfo(result);
        } catch (ServiceUnavailableException e) {
            log.error("Не удалось разослать задачу requestId={}: {}", requestId, e.getMessage());
        }
    }

    private void updateRecipientDeliveryInfo(TaskDistributionResultDto result) {
        if (result.getDeliveries() == null || result.getDeliveries().isEmpty()) {
            return;
        }

        Map<Long, TaskDistributionResultDto.RecipientDeliveryResultDto> deliveriesByRecipientId =
                result.getDeliveries().stream()
                        .collect(Collectors.toMap(
                                TaskDistributionResultDto.RecipientDeliveryResultDto::getRecipientId,
                                delivery -> delivery));

        List<RequestRecipient> recipients = requestRecipientRepository.findAllById(deliveriesByRecipientId.keySet());
        for (RequestRecipient recipient : recipients) {
            TaskDistributionResultDto.RecipientDeliveryResultDto delivery =
                    deliveriesByRecipientId.get(recipient.getId());
            if (delivery != null) {
                recipient.setExternalChatId(delivery.getChatId());
            }
        }
        requestRecipientRepository.saveAll(recipients);
    }
}
