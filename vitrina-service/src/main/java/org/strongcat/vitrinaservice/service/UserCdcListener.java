package org.strongcat.vitrinaservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.vitrinaservice.data.entity.UserDimension;
import org.strongcat.vitrinaservice.data.repository.SkillDimensionRepository;
import org.strongcat.vitrinaservice.data.repository.UserDimensionRepository;
import org.strongcat.vitrinaservice.dto.DebeziumEventDto;
import org.strongcat.vitrinaservice.dto.DebeziumSkillEventDto;
import org.strongcat.vitrinaservice.dto.DebeziumUserPayloadDto;
import org.strongcat.vitrinaservice.dto.DebeziumUserSkillEventDto;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCdcListener {

    private final UserDimensionRepository userDimensionRepository;
    private final SkillDimensionRepository skillDimensionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(id = "user-cdc-consumer", topics = "cdc.public.users", groupId = "analytics-cdc-group",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenUserChanges(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) {
                return;
            }

            DebeziumEventDto event = objectMapper.treeToValue(recordRoot.get("payload"), DebeziumEventDto.class);
            log.info("CDC USERS: received operation {}", event.getOp());

            if ("c".equals(event.getOp()) || "u".equals(event.getOp())) {
                var userData = event.getAfter();

                UserDimension newUserDim = createBaseDimension(userData);
                userDimensionRepository.save(newUserDim);
                log.info("CDC USERS: created dimension for user {}", userData.getUsername());
            }
        } catch (Exception e) {
            log.error("CDC USERS: error while processing Kafka event", e);
        }
    }

    private UserDimension createBaseDimension(DebeziumUserPayloadDto userData) {
        UserDimension userDim = new UserDimension();
        userDim.setIdNaturalUser(userData.getId().intValue());
        userDim.setUsername(userData.getUsername());

        String fullName = Stream.of(userData.getLastName(), userData.getFirstName(), userData.getMiddleName())
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining(" "));
        userDim.setFullName(fullName);

        userDim.setSpecialization(userData.getSpecialization());
        userDim.setExperience(userData.getExperience());

        if (userData.getBirthDateDays() != null) {
            userDim.setBirthDate(LocalDate.ofEpochDay(userData.getBirthDateDays()));
        }
        return userDim;
    }

    @KafkaListener(id = "skill-cdc-consumer", topics = "cdc.public.skill", groupId = "analytics-cdc-group",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenSkillChanges(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) {
                return;
            }

            var event = objectMapper.treeToValue(recordRoot.get("payload"), DebeziumSkillEventDto.class);

            if ("c".equals(event.getOp()) || "u".equals(event.getOp())) {
                var skillData = event.getAfter();
                Long skillId = skillData.getId();

                skillDimensionRepository.upsertSkill(skillId, skillData.getName());
                log.info("CDC SKILLS: d_skill upserted by natural ID {}: {}", skillId, skillData.getName());
            }
        } catch (Exception e) {
            log.error("CDC SKILLS: error while processing skill change", e);
        }
    }

    @KafkaListener(id = "user-skill-cdc-consumer", topics = "cdc.public.users_skills", groupId = "analytics-cdc-group",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenUserSkillLinkChanges(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) {
                return;
            }

            var event = objectMapper.treeToValue(recordRoot.get("payload"), DebeziumUserSkillEventDto.class);
            var linkData = "d".equals(event.getOp()) ? event.getBefore() : event.getAfter();

            UserDimension userDim = userDimensionRepository.findFirstByIdNaturalUserOrderByUserKeyDesc(
                    linkData.getUserId().intValue()).orElse(null);

            if (userDim == null) {
                log.warn("CDC USER_SKILLS: user with natural ID {} is not found in d_user, link skipped",
                        linkData.getUserId());
                return;
            }

            if ("c".equals(event.getOp())) {
                int inserted = userDimensionRepository.insertUserSkillLink(userDim.getUserKey(), linkData.getSkillId());
                log.info("CDC USER_SKILLS: inserted link userKey={}, skillKey={}, rows={}",
                        userDim.getUserKey(), linkData.getSkillId(), inserted);
            } else if ("d".equals(event.getOp())) {
                int deleted = userDimensionRepository.deleteUserSkillLink(userDim.getUserKey(), linkData.getSkillId());
                log.info("CDC USER_SKILLS: deleted link userKey={}, skillKey={}, rows={}",
                        userDim.getUserKey(), linkData.getSkillId(), deleted);
            }
        } catch (Exception e) {
            log.error("CDC USER_SKILLS: error while processing user-skill link", e);
        }
    }
}
