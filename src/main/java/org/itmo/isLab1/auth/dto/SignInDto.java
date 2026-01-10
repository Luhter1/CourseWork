package org.itmo.isLab1.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInDto {
    @Size(min = 3, max = 255, message = "Имя пользователя должно содержать от 3 до 255 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Size(min = 6, max = 128, message = "Длина пароля должна быть от 6 до 128")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
