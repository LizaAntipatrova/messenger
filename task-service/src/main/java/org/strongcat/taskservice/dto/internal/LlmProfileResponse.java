package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LlmProfileResponse {
    @JsonProperty("experience_months")
    private Integer experienceMonths;
    @JsonProperty("min_rate_rub")
    private BigDecimal minRateRub;
    @JsonProperty("skills")
    private List<LlmSkillResponse> skills;

}

