package org.itmo.isLab1.residences.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResidenceProfileCreateDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private final String title;

    private final String description;

    @NotBlank(message = "Местоположение не может быть пустым")
    @Size(max = 255, message = "Местоположение должно содержать максимум 255 символов")
    private final String location;

    @NotBlank(message = "Контакты не могут быть пустыми")
    @Size(max = 1000, message = "Контакты должны содержать максимум 1000 символов")
    private final String contacts;

    @NotNull(message = "Статус публикации обязателен")
    private final Boolean isPublished;
}