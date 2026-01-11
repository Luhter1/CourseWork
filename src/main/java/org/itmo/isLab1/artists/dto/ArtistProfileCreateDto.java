package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistProfileCreateDto {
    @Size(max = 500, message = "Биография должна содержать максимум 500 символов")
    private final String biography;

    @Size(max = 200, message = "Местоположение должно содержать максимум 200 символов")
    private final String location;
}
