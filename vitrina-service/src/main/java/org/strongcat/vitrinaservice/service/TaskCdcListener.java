package org.strongcat.vitrinaservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.vitrinaservice.data.entity.DateDimension;
import org.strongcat.vitrinaservice.data.entity.TaskDimension;
import org.strongcat.vitrinaservice.data.entity.UserDimension;
import org.strongcat.vitrinaservice.data.entity.UserFact;
import org.strongcat.vitrinaservice.data.repository.DateDimensionRepository;
import org.strongcat.vitrinaservice.data.repository.SkillDimensionRepository;
import org.strongcat.vitrinaservice.data.repository.TaskDimensionRepository;
import org.strongcat.vitrinaservice.data.repository.UserDimensionRepository;
import org.strongcat.vitrinaservice.data.repository.UserFactRepository;
import org.strongcat.vitrinaservice.dto.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCdcListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TaskDimensionRepository taskDimensionRepository;
    private final SkillDimensionRepository skillDimensionRepository;
    private final UserDimensionRepository userDimensionRepository;
    private final UserFactRepository userFactRepository;
    private final DateDimensionRepository dateDimensionRepository;

    // cache: requestId -> specialistExternalUserId
    private final Map<Long, Long> requestAssigneeCache = new ConcurrentHashMap<>();
    // cache: specialistId -> externalUserId
    private final Map<Long, Long> specialistIdToExternal = new ConcurrentHashMap<>();
    // cache status id -> name (for request and response statuses)
    private final Map<Long, String> statusIdToName = new ConcurrentHashMap<>();

    @KafkaListener(id = "task-request-consumer", topics = "cdc.public.request",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenRequest(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) {
                return;
            }

            DebeziumRequestEventDto event = objectMapper.treeToValue(recordRoot.get("payload"), DebeziumRequestEventDto.class);
            log.info("CDC REQUEST: op={}", event.getOp());

            if ("c".equals(event.getOp())) {
                var after = event.getAfter();
                if (after == null) return;
                Integer naturalId = after.getId().intValue();

                TaskDimension td = taskDimensionRepository.findByIdNaturalTask(naturalId)
                        .orElseGet(() -> {
                            TaskDimension newTd = new TaskDimension();
                            newTd.setIdNaturalTask(naturalId);
                            newTd.setStartDate(LocalDate.now());
                            return newTd;
                        });

                if (after.getPayment() != null) td.setBudget(after.getPayment().floatValue());
                if (after.getExpectedDurationDays() != null) td.setDuration(after.getExpectedDurationDays().floatValue());
                if (after.getSpecializationId() != null) td.setCategoryId(after.getSpecializationId());

                taskDimensionRepository.save(td);
                log.info("CDC REQUEST: d_task created for request {}", naturalId);

            } else if ("u".equals(event.getOp())) {
                var after = event.getAfter();
                if (after == null) return;
                Integer naturalId = after.getId().intValue();

                TaskDimension td = taskDimensionRepository.findByIdNaturalTask(naturalId).orElse(null);
                if (td != null) {
                    if (after.getPayment() != null) td.setBudget(after.getPayment().floatValue());
                    if (after.getExpectedDurationDays() != null) td.setDuration(after.getExpectedDurationDays().floatValue());
                    if (after.getSpecializationId() != null) td.setCategoryId(after.getSpecializationId());

                    taskDimensionRepository.save(td);
                    log.info("CDC REQUEST: d_task updated for request {}", naturalId);
                } else {
                    log.warn("CDC REQUEST: try to update d_task, but it was not found for request {}", naturalId);
                }

                Long statusId = after.getRequestStatusId();
                String statusName = statusId != null ? statusIdToName.get(statusId) : null;

                if ("COMPLETED".equals(statusName)) {
                    Long specialistExternal = requestAssigneeCache.get(naturalId.longValue());
                    if (specialistExternal == null) {
                        log.warn("CDC REQUEST: no cached assignee for completed request {}", naturalId);
                        return;
                    }

                    if (td == null) {
                        td = taskDimensionRepository.findByIdNaturalTask(naturalId).orElse(null);
                    }

                    if (td == null) {
                        log.warn("CDC REQUEST: task dimension not found for request {}", naturalId);
                        return;
                    }

                    UserDimension userDim = userDimensionRepository.findFirstByIdNaturalUserOrderByUserKeyDesc(
                            specialistExternal.intValue())
                            .orElse(null);
                    if (userDim == null) {
                        log.warn("CDC REQUEST: user dimension not found for external user {}", specialistExternal);
                        return;
                    }

                    LocalDate today = LocalDate.now();
                    DateDimension dateDim = dateDimensionRepository.findByFullDate(today)
                            .orElseGet(() -> {
                                DateDimension dd = new DateDimension();
                                dd.setFullDate(today);
                                dd.setDayOfWeek((short) today.getDayOfWeek().getValue());
                                dd.setMonthNumber((short) today.getMonthValue());
                                return dateDimensionRepository.save(dd);
                            });

                    UserFact fact = UserFact.builder()
                            .userDimension(userDim)
                            .taskDimension(td)
                            .dateDimension(dateDim)
                            .build();

                    userFactRepository.save(fact);
                    log.info("CDC REQUEST: fact_user saved for request {}, user {}", naturalId, specialistExternal);

                    Long currentUserKey = userDim.getUserKey();
                    userDimensionRepository.updatePreferedComplexity(currentUserKey);
                    userDimensionRepository.updateTopTaskCategories(currentUserKey);
                    log.info("CDC REQUEST: d_user calculated fields updated for userKey {}", currentUserKey);
                }
            }
        } catch (Exception e) {
            log.error("CDC REQUEST: error processing event", e);
        }
    }

    @KafkaListener(id = "task-request-recipient-consumer", topics = "cdc.public.request_recipient",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenRequestRecipient(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) return;

            DebeziumRequestRecipientEventDto event = objectMapper.treeToValue(recordRoot.get("payload"),
                    DebeziumRequestRecipientEventDto.class);
            log.info("CDC REQUEST_RECIPIENT: op={}", event.getOp());

            var payload = "d".equals(event.getOp()) ? event.getBefore() : event.getAfter();
            if (payload == null) return;

            Long responseStatusId = payload.getResponseStatusId();
            String responseStatusName = responseStatusId != null ? statusIdToName.get(responseStatusId) : null;
            if ("ACCEPTED".equals(responseStatusName)) {
                Long requestId = payload.getRequestId();
                Long specialistId = payload.getSpecialistId();
                Long specialistExternal = specialistIdToExternal.get(specialistId);
                if (specialistExternal == null) {
                    log.warn("CDC REQUEST_RECIPIENT: external id missing for specialist {} — caching specialist id only",
                            specialistId);
                    requestAssigneeCache.put(requestId, specialistId);
                } else {
                    requestAssigneeCache.put(requestId, specialistExternal);
                }
                log.info("CDC REQUEST_RECIPIENT: cached assignee for request {} -> {}", requestId,
                        specialistExternal != null ? specialistExternal : specialistId);
            } else if ("d".equals(event.getOp())) {
                var before = event.getBefore();
                if (before != null) {
                    Long requestId = before.getRequestId();
                    requestAssigneeCache.remove(requestId);
                }
            }
        } catch (Exception e) {
            log.error("CDC REQUEST_RECIPIENT: error processing event", e);
        }
    }

    @KafkaListener(id = "task-specialist-consumer", topics = "cdc.public.specialist",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    public void listenSpecialist(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) return;
            JsonNode payload = recordRoot.get("payload");
            JsonNode after = payload.get("after");
            if (after == null || after.isNull()) return;
            Long id = after.get("id").asLong();
            Long externalUserId = after.has("external_user_id") &&
                    !after.get("external_user_id").isNull() ? after.get("external_user_id").asLong() : null;
            if (externalUserId != null) {
                specialistIdToExternal.put(id, externalUserId);
                log.info("CDC SPECIALIST: mapped specialist {} -> external {}", id, externalUserId);
            }
        } catch (Exception e) {
            log.error("CDC SPECIALIST: error", e);
        }
    }

    @KafkaListener(id = "task-request-skill-consumer", topics = "cdc.public.request_skill",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    @Transactional
    public void listenRequestSkill(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) return;
            DebeziumRequestSkillEventDto event = objectMapper.treeToValue(recordRoot.get("payload"),
                    DebeziumRequestSkillEventDto.class);
            var payload = "d".equals(event.getOp()) ? event.getBefore() : event.getAfter();
            if (payload == null) return;

            Long requestId = payload.getRequestId();
            Long skillId = payload.getSkillId();

            var taskDimOpt = taskDimensionRepository.findByIdNaturalTask(requestId.intValue());
            var skillDimOpt = skillDimensionRepository.findById(skillId);
            if (taskDimOpt.isPresent() && skillDimOpt.isPresent()) {
                TaskDimension taskDim = taskDimOpt.get();
                taskDim.getSkills().add(skillDimOpt.get());
                taskDimensionRepository.save(taskDim);
                log.info("CDC REQUEST_SKILL: linked skill {} to task {}", skillId, requestId);
            } else {
                log.warn("CDC REQUEST_SKILL: missing task or skill for request {} skill {}", requestId, skillId);
            }
        } catch (Exception e) {
            log.error("CDC REQUEST_SKILL: error", e);
        }
    }

    @KafkaListener(id = "task-request-status-consumer", topics = "cdc.public.request_status",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    public void listenRequestStatus(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) return;
            JsonNode payload = recordRoot.get("payload");
            JsonNode after = payload.get("after");
            if (after == null || after.isNull()) return;
            Long id = after.get("id").asLong();
            String name = after.has("name") && !after.get("name").isNull() ? after.get("name").asText() : null;
            if (name != null) {
                statusIdToName.put(id, name);
                log.info("CDC REQUEST_STATUS: {} -> {}", id, name);
            }
        } catch (Exception e) {
            log.error("CDC REQUEST_STATUS: error", e);
        }
    }

    @KafkaListener(id = "task-response-status-consumer", topics = "cdc.public.response_status",
            properties = {"spring.json.value.default.type=com.fasterxml.jackson.databind.JsonNode"})
    public void listenResponseStatus(JsonNode recordRoot) {
        try {
            if (!recordRoot.has("payload") || recordRoot.get("payload").isNull()) return;
            JsonNode payload = recordRoot.get("payload");
            JsonNode after = payload.get("after");
            if (after == null || after.isNull()) return;
            Long id = after.get("id").asLong();
            String name = after.has("name") && !after.get("name").isNull() ? after.get("name").asText() : null;
            if (name != null) {
                statusIdToName.put(id, name);
                log.info("CDC RESPONSE_STATUS: {} -> {}", id, name);
            }
        } catch (Exception e) {
            log.error("CDC RESPONSE_STATUS: error", e);
        }
    }
}
