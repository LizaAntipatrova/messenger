package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumUserSkillPayloadDto {
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("skill_id")
    private Long skillId;
}