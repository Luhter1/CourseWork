package org.itmo.isLab1.notifications.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.notifications.dto.NotificationDto;
import org.itmo.isLab1.notifications.entity.Notification;
import org.itmo.isLab1.notifications.entity.NotificationCategory;
import org.itmo.isLab1.notifications.mapper.NotificationMapper;
import org.itmo.isLab1.notifications.repository.NotificationRepository;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long sendInviteNotification(NotificationCreateDto dto) {
        // Проверяем, что пользователь существует
        userRepository.findByUsername(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + dto.getEmail() + " не найден"));

        Long notificationId = notificationRepository.createNotification(
                dto.getEmail(),
                dto.getMessage(),
                dto.getCategory().name(),
                dto.getLink()
            );

        return notificationId;
    }

    @Transactional
    public Long sendNotification(
        String email, String message, NotificationCategory category, String link) {
        // Проверяем, что пользователь существует
        userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + email + " не найден"));

        Long notificationId = notificationRepository.createNotification(
            email,
            message,
            category.name(),
            link
        );

        return notificationId;
    }

    /**
     * Получение пагинированного списка уведомлений пользователя
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница с уведомлениями
     */
    public Page<NotificationDto> getNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toDto);
    }

    /**
     * Получение количества непрочитанных уведомлений пользователя
     *
     * @param userId идентификатор пользователя
     * @return количество непрочитанных уведомлений
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadAtIsNull(userId);
    }

    /**
     * Пометка конкретного уведомления как прочитанного с проверкой принадлежности пользователю
     *
     * @param notificationId идентификатор уведомления
     * @param userId        идентификатор пользователя
     * @throws ResourceNotFoundException если уведомление не найдено или не принадлежит пользователю
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        // Проверяем, что уведомление принадлежит пользователю
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Уведомление не найдено или не принадлежит пользователю"));

        // Если уже прочитано, ничего не делаем
        if (notification.getReadAt() != null) {
            return;
        }

        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException(
                    "Уведомление не найдено или не принадлежит пользователю");
        }
    }

    /**
     * Пометка всех уведомлений пользователя как прочитанных
     *
     * @param userId идентификатор пользователя
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
