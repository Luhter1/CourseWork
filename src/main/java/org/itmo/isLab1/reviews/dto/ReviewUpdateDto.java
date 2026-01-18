package org.itmo.isLab1.reviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {

    @Min(value = 1, message = "Оценка от 1 до 10")
    @Max(value = 10, message = "Оценка от 1 до 10")
    private Integer score;

    @Size(max = 1000, message = "Комментарий не более 1000 символов")
    private String comment;
}
