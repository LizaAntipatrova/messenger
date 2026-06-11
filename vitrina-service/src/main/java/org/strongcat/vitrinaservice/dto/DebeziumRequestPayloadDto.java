package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumRequestPayloadDto {
    private Long id;
    private BigDecimal payment;

    @JsonProperty("expected_duration_days")
    private Long expectedDurationDays;
    @JsonProperty("specialization_id")
    private Integer specializationId;
    @JsonProperty("request_status_id")
    private Long requestStatusId;
}
