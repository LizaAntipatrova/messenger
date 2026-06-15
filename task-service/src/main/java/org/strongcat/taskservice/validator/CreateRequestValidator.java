package org.strongcat.taskservice.validator;

import org.springframework.stereotype.Component;
import org.strongcat.taskservice.dto.CreateRequestDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс валидации заявки.
*/
@Component
public class CreateRequestValidator {
    /**
     * Проверяет корректность данных новой заявки.
     *
     * @param dto DTO создания заявки
     */
    public void validate(CreateRequestDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto == null) {
            throw new IllegalArgumentException("Тело запроса не может быть пустым");
        }
        validateSpecialization(dto);
        validateDescription(dto);
        validatePayment(dto);
        validateExperience(dto);
        validateExpectedDuration(dto);
    }
    private void validateSpecialization(CreateRequestDto dto) {
        if (dto.getSpecializationId() == null) {
            throw new IllegalArgumentException("Специализация обязательна");
        } else if (dto.getSpecializationId() <= 0) {
            throw new IllegalArgumentException("Идентификатор специализации должен быть положительным числом");
        }
    }
    private void validateDescription(CreateRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание задачи не может быть пустым");
        }
        if (dto.getDescription().length() < 10) {
            throw new IllegalArgumentException("Описание задачи должно содержать не менее 10 символов");
        }
        if (dto.getDescription().length() > 5000) {
            throw new IllegalArgumentException("Описание задачи не должно превышать 5000 символов");
        }
    }
    private void validatePayment(CreateRequestDto dto) {
        if (dto.getPayment() == null) {
            throw new IllegalArgumentException("Оплата обязательна");
        }
        if (dto.getPayment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Оплата должна быть положительным числом");
        }
    }
    private void validateExperience(CreateRequestDto dto) {
        Integer experience = dto.getRequiredExperience();
        if (experience != null && experience < 0) {
            throw new IllegalArgumentException("Требуемый опыт не может быть отрицательным");
        }
    }
    private void validateExpectedDuration(CreateRequestDto dto) {
        Long duration = dto.getExpectedDurationDays();
        if (duration != null && duration <= 0) {
            throw new IllegalArgumentException("Ожидаемая длительность должна быть положительным числом");
        }
    }
}
