package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
    public class LlmSkillResponse {
        @JsonProperty("name")
        private String name;
        @JsonProperty("weight")
        private BigDecimal weight;
    }