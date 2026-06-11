package org.strongcat.vitrinaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalyticsDto {
    private Integer idNaturalUser;
    private Float averagePayment;
    private Float preferedComplexity;
    private LocalDate lastActivityDate;
    private Float taskExperienceHours;
}