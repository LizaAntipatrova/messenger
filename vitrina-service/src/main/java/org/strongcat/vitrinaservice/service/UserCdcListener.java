package org.strongcat.vitrinaservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.vitrinaservice.data.entity.UserDimension;
import org.strongcat.vitrinaservice.data.repository.UserDimensionRepository;
import org.strongcat.vitrinaservice.dto.DebeziumEventDto;
import org.strongcat.vitrinaservice.dto.DebeziumUserPayloadDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCdcListener {

    private final UserDimensionRepository userDimensionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "cdc.public.users",
            groupId = "analytics-cdc-group",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"}
    )
    @Transactional
    public void listenUserChanges(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) {
                return;
            }

            DebeziumEventDto event = objectMapper.treeToValue(recordRoot.get("payload"), DebeziumEventDto.class);
            log.info("CDC СЛУШАТЕЛЬ: Получено CDC-событие базы данных, операция: {}", event.getOp());

            if ("c".equals(event.getOp()) || "u".equals(event.getOp())) {
                var userData = event.getAfter();

                UserDimension newUserDim = createBaseDimension(userData);
                userDimensionRepository.save(newUserDim);
                log.info("CDC СЛУШАТЕЛЬ: Создано новое измерение для пользователя: {}", userData.getUsername());
            }

        } catch (Exception e) {
            log.error("CDC СЛУШАТЕЛЬ: Ошибка при обработке CDC-события из Kafka!", e);
        }
    }

    private UserDimension createBaseDimension(DebeziumUserPayloadDto userData) {
        UserDimension userDim = new UserDimension();
        userDim.setIdNaturalUser(userData.getId().intValue());
        userDim.setUsername(userData.getUsername());
        userDim.setFullName(userData.getFullName());
        userDim.setSpecialization(userData.getSpecialization());
        userDim.setExperience(userData.getExperience());

        if (userData.getBirthDateDays() != null) {
            userDim.setBirthDate(LocalDate.ofEpochDay(userData.getBirthDateDays()));
        }
        return userDim;
    }
}