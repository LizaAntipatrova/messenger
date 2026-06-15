package org.strongcat.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для принятия или отклонения адресного запроса специалистом.
 * 
 * Валидация выполняется вручную в сервисном слое.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistDecisionDto {
    /**
     * Внешний идентификатор специалиста.
     */
    private Long specialistExternalUserId;
}
