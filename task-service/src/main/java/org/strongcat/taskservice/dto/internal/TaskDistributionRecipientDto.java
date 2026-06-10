package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDistributionRecipientDto {
    private Long recipientId;
    private Long specialistExternalUserId;
    private Integer rankPosition;
}
