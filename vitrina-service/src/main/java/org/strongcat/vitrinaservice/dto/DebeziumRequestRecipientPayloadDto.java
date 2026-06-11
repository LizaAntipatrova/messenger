package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestRecipientPayloadDto {
    private Long id;
    @JsonProperty("request_id")
    private Long requestId;
    @JsonProperty("specialist_id")
    private Long specialistId;
    @JsonProperty("response_status_id")
    private Long responseStatusId;
}
