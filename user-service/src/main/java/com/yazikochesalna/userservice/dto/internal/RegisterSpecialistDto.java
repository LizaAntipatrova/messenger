package com.yazikochesalna.userservice.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterSpecialistDto {
    private Long externalUserId;
    private String specializationName;
    private Integer experienceMonths;
    private List<Long> skillIds;
}
