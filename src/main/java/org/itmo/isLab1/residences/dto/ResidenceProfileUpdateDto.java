package org.itmo.isLab1.residences.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Builder
public class ResidenceProfileUpdateDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private final JsonNullable<String> title;

    private final JsonNullable<String> description;

    @NotBlank(message = "Местоположение не может быть пустым")
    @Size(max = 255, message = "Местоположение должно содержать максимум 255 символов")
    private final JsonNullable<String> location;

    @NotBlank(message = "Контакты не могут быть пустыми")
    @Size(max = 500, message = "Контакты должны содержать максимум 500 символов")
    private final JsonNullable<String> contacts;

    @NotNull(message = "Статус публикации обязателен")
    private final JsonNullable<Boolean> isPublished;
}