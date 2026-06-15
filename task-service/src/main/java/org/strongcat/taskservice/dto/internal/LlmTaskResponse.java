package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LlmTaskResponse {
    @JsonProperty("required_experience_months")
    private Integer requiredExperienceMonths;
    @JsonProperty("payment")
    private BigDecimal payment;
    @JsonProperty("expected_duration_days")
    private Long expectedDurationDays;
    @JsonProperty("skills")
    private List<LlmSkillResponse> skills;


}

