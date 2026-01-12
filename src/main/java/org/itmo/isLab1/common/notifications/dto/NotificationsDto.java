package org.itmo.isLab1.common.notifications.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NotificationsDto {
    @NotBlank
    private String email;

    @NotBlank
    private String message;

    @NotBlank
    @Pattern(regexp = "system|invite|review|status", message = "Неверная категория")
    private String category;

    @URL(message = "Ссылка должна быть корректным URL")
    private String link;
}
