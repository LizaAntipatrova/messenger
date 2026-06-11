package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestRecipientEventDto {
    private DebeziumRequestRecipientPayloadDto before;
    private DebeziumRequestRecipientPayloadDto after;
    private String op; // c, u, d
}
