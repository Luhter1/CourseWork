package org.itmo.isLab1.residences.dto;

import java.util.Map;

import org.openapitools.jackson.nullable.JsonNullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceDetailsUpdateDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private JsonNullable<String> title;

    @Size(max = 5000)
    private JsonNullable<String> description;

    @NotBlank(message = "Местоположение не может быть пустым")
    @Size(max = 255, message = "Местоположение должно содержать максимум 255 символов")
    private JsonNullable<String> location;

    @NotNull(message = "Контакты не могут быть пустыми")
    private JsonNullable<Map<String, Object>> contacts;
}