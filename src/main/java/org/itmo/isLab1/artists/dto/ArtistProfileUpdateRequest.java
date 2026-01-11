package org.itmo.isLab1.artists.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistProfileUpdateRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 100, message = "Имя должно содержать максимум 100 символов")
    private final String name;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 100, message = "Фамилия должна содержать максимум 100 символов")
    private final String surname;

    @Size(max = 2000, message = "Биография должна содержать максимум 2000 символов")
    private final String biography;

    @Size(max = 200, message = "Местоположение должно содержать максимум 200 символов")
    private final String location;
}
