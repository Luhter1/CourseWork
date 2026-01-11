package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.itmo.isLab1.artists.entity.ArtDirectionEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequest {
    
    @NotBlank(message = "Название работы не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private String title;
    
    @Size(max = 500, message = "Описание должно содержать максимум 500 символов")
    private String description;
    
    @NotNull(message = "Направление искусства обязательно")
    private ArtDirectionEnum artDirection;
    
    @NotNull(message = "Дата создания работы обязательна")
    @PastOrPresent(message = "Дата создания работы не может быть в будущем")
    private LocalDate date;
    
    @URL(message = "Ссылка должна быть корректным URL")
    private String link;
}
