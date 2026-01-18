package org.itmo.isLab1.common.notifications.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;
import org.itmo.isLab1.users.User;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_notifications")
public class Notification implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_notifications_id_seq")
    @SequenceGenerator(name = "art2art_notifications_id_seq", sequenceName = "art2art_notifications_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "category", nullable = false)
    @NotNull(message = "Notification category is required")
    @ColumnTransformer(write="?::art2art_notification_category")
    private NotificationCategory category;

    @Column(name = "read_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;
}
