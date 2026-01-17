package org.itmo.isLab1.common.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private String link;
    private String category;
    private ZonedDateTime readAt;
    private ZonedDateTime createdAt;
}
