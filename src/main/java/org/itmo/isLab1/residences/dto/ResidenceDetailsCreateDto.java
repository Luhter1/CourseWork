package org.itmo.isLab1.residences.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceDetailsCreateDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private String title;

    @Size(max = 5000)
    private String description;

    @NotBlank(message = "Местоположение не может быть пустым")
    @Size(max = 255, message = "Местоположение должно содержать максимум 255 символов")
    private String location;

    @NotNull(message = "Контакты не могут быть пустыми")
    private Map<String, Object> contacts;
}