package org.strongcat.vitrinaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumUserPayloadDto {
    private Long id;
    private String username;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty("birth_date")
    private Long birthDateDays; 
    
    private String specialization;
    private Float experience;
}