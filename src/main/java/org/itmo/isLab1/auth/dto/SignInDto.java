package org.itmo.isLab1.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInDto {
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @Size(min = 3, max = 255, message = "Email пользователя должен содержать от 3 до 255 символов")
    @NotBlank(message = "Email пользователя не может быть пустым")
    private String email;

    @Size(min = 8, max = 128, message = "Длина пароля должна быть от 8 до 128 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
