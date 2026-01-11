package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistProfileCreateDto {
    @Size(max = 2000, message = "Биография должна содержать максимум 2000 символов")
    private final String biography;

    @Size(max = 200, message = "Местоположение должно содержать максимум 200 символов")
    private final String location;
}
