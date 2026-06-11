package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestSkillPayloadDto {
    private Long id;
    @JsonProperty("request_id")
    private Long requestId;
    @JsonProperty("skill_id")
    private Long skillId;
}
