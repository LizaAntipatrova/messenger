package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestEventDto {
    private DebeziumRequestPayloadDto before;
    private DebeziumRequestPayloadDto after;
    private String op; // c, u, d
}
