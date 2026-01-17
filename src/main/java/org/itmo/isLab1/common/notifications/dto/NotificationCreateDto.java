package org.itmo.isLab1.common.notifications.dto;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.validator.constraints.URL;
import org.itmo.isLab1.common.notifications.entity.NotificationCategory;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class NotificationCreateDto {
    @NotBlank
    private String email;

    @NotBlank
    private String message;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "category", nullable = false)
    @NotNull(message = "Category is required")
    @ColumnTransformer(write="?::art2art_notification_category")
    private NotificationCategory category;

    @URL(message = "Ссылка должна быть корректным URL")
    private String link;
}
