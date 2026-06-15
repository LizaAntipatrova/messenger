package org.strongcat.taskservice.validator;

import org.springframework.stereotype.Component;
import org.strongcat.taskservice.dto.SpecialistDecisionDto;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Валидатор решения специалиста по заявке.
 */
@Component
public class SpecialistDecisionValidator {
    /**
     * Проверяет DTO принятия или отклонения заявки.
     */
    public void validate(SpecialistDecisionDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Тело запроса не может быть пустым");
        }
        if (dto.getSpecialistExternalUserId() == null) {
            throw  new IllegalArgumentException("Идентификатор специалиста обязателен");
        } else if (dto.getSpecialistExternalUserId() <= 0) {
            throw  new IllegalArgumentException("Идентификатор специалиста должен быть положительным числом");
        }
    }
}
