package org.itmo.isLab1.artists.dto;

import org.openapitools.jackson.nullable.JsonNullable;

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
public class WorkUpdateDto {
    
    @NotBlank(message = "Название работы не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    private JsonNullable<String> title;
    
    @Size(max = 500, message = "Описание должно содержать максимум 500 символов")
    private JsonNullable<String> description;
    
    @NotNull(message = "Направление искусства обязательно")
    private JsonNullable<ArtDirectionEnum> artDirection;
    
    @NotNull(message = "Дата создания работы обязательна")
    @PastOrPresent(message = "Дата создания работы не может быть в будущем")
    private JsonNullable<LocalDate> date;
    
    @URL(message = "Ссылка должна быть корректным URL")
    private JsonNullable<String> link;
}
