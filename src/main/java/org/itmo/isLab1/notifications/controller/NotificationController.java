package org.itmo.isLab1.notifications.controller;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.notifications.dto.NotificationDto;
import org.itmo.isLab1.notifications.service.NotificationService;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления уведомлениями пользователей
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Получение пагинированного списка уведомлений текущего пользователя
     *
     * @param pageable параметры пагинации
     * @return страница с уведомлениями
     */
    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<NotificationDto> notifications = notificationService.getNotifications(currentUser.getId(), pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Получение количества непрочитанных уведомлений текущего пользователя
     *
     * @return количество непрочитанных уведомлений
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        User currentUser = getCurrentUser();
        long unreadCount = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(unreadCount);
    }

    /**
     * Пометка всех уведомлений текущего пользователя как прочитанных
     *
     * @return HTTP 204 No Content
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        User currentUser = getCurrentUser();
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Пометка конкретного уведомления как прочитанного
     *
     * @param id идентификатор уведомления
     * @return HTTP 204 No Content
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        notificationService.markAsRead(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из контекста безопасности
     *
     * @return текущий пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));
    }
}
