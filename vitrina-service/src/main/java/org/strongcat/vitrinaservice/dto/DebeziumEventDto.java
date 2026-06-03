package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumEventDto {
    private DebeziumUserPayloadDto before;
    private DebeziumUserPayloadDto after;
    private String op; // c - create, u - update, d - delete
}