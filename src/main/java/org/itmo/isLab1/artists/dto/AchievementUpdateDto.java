package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementUpdateDto {
    
    @NotBlank(message = "Название достижения не может быть пустым")
    @Size(min = 1, max = 255, message = "Название должно содержать от 1 до 255 символов")
    private JsonNullable<String> title;
    
    @Size(max = 500, message = "Описание должно содержать максимум 500 символов")
    private JsonNullable<String> description;
    
    @URL(message = "Ссылка должна быть корректным URL")
    private JsonNullable<String> link;
}
