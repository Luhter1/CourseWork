package org.itmo.isLab1.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import org.itmo.isLab1.notifications.entity.NotificationCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private String link;
    private NotificationCategory category;
    private ZonedDateTime readAt;
    private ZonedDateTime createdAt;
}
