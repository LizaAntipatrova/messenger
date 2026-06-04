package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumSkillEventDto {
    private DebeziumSkillPayloadDto before;
    private DebeziumSkillPayloadDto after;
    private String op; // c, u, d
}