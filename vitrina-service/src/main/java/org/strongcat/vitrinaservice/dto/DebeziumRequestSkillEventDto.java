package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestSkillEventDto {
    private DebeziumRequestSkillPayloadDto before;
    private DebeziumRequestSkillPayloadDto after;
    private String op; // c, u, d
}
