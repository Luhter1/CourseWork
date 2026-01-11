package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.itmo.isLab1.artists.entity.AchievementTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementCreateDto {
    
    @NotNull(message = "Тип достижения обязателен")
    private AchievementTypeEnum type;
    
    @NotBlank(message = "Название достижения не может быть пустым")
    @Size(min = 1, max = 255, message = "Название должно содержать от 1 до 255 символов")
    private String title;
    
    @Size(max = 500, message = "Описание должно содержать максимум 500 символов")
    private String description;
    
    @URL(message = "Ссылка должна быть корректным URL")
    private String link;
}
