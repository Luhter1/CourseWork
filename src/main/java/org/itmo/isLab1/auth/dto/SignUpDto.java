package org.itmo.isLab1.auth.dto;

import org.itmo.isLab1.users.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpDto {
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @Size(min = 3, max = 255, message = "Email пользователя должен содержать от 3 до 255 символов")
    @NotBlank(message = "Email пользователя не может быть пустым")
    private String email;

    @Size(min = 3, max = 255, message = "Имя пользователя должно содержать от 3 до 255 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @Size(min = 3, max = 255, message = "Фамилия пользователя должна содержать от 3 до 255 символов")
    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    private String surname;

    @Size(min = 8, max = 128, message = "Длина пароля должна быть от 8 до 128 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotNull(message = "Роль пользователя должна быть указана")
    private Role role;
}
