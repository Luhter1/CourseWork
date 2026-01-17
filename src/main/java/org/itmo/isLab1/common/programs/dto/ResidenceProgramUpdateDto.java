package org.itmo.isLab1.common.programs.dto;

import java.time.LocalDate;
import java.util.Map;

import org.openapitools.jackson.nullable.JsonNullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceProgramUpdateDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private JsonNullable<String> title;

    @Size(max = 5000)
    private JsonNullable<String> description;

    private JsonNullable<Map<String, Object>> goals;

    private JsonNullable<Map<String, Object>> conditions;

    @NotNull(message = "Дедлайн подачи не может быть пустым")
    private JsonNullable<LocalDate> deadlineApply;

    @NotNull(message = "Дедлайн ревью не может быть пустым")
    private JsonNullable<LocalDate> deadlineReview;

    @NotNull(message = "Дедлайн уведомления не может быть пустым")
    private JsonNullable<LocalDate> deadlineNotify;

    @PositiveOrZero(message = "Длительность должна быть неотрицательным числом")
    private JsonNullable<Integer> durationDays;

    @PositiveOrZero(message = "Бюджет должен быть неотрицательным числом")
    private JsonNullable<Integer> budgetQuota;

    @PositiveOrZero(message = "Квота участников должна быть неотрицательным числом")
    private JsonNullable<Integer> peopleQuota;

    private JsonNullable<Boolean> isPublished;
}
