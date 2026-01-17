package org.itmo.isLab1.common.notifications.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.common.notifications.dto.NotificationDto;
import org.itmo.isLab1.common.notifications.entity.Notification;
import org.itmo.isLab1.common.notifications.mapper.NotificationMapper;
import org.itmo.isLab1.common.notifications.repository.NotificationRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.itmo.isLab1.users.UserService;
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
    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long sendInviteNotification(NotificationCreateDto dto) {
        // Проверяем, что пользователь существует
        userRepository.findByUsername(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + dto.getEmail() + " не найден"));

        // Вызываем SQL-функцию
        Long notificationId = (Long) entityManager.createNativeQuery(
                "SELECT create_notification_by_email(:email, :message, :category, :link)")
                .setParameter("email", dto.getEmail())
                .setParameter("message", dto.getMessage())
                .setParameter("category", dto.getCategory())
                .setParameter("link", dto.getLink())
                .getSingleResult();

        return notificationId;
    }

    /**
     * Получение пагинированного списка уведомлений пользователя с сортировкой по дате создания (DESC)
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница с уведомлениями
     */
    public Page<NotificationDto> getNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
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
                        "Уведомление с id " + notificationId + " не найдено или не принадлежит пользователю"));

        // Если уже прочитано, ничего не делаем
        if (notification.getReadAt() != null) {
            return;
        }

        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException(
                    "Уведомление с id " + notificationId + " не найдено или не принадлежит пользователю");
        }
    }

    /**
     * Пометка всех уведомлений пользователя как прочитанных в рамках одной транзакции
     *
     * @param userId идентификатор пользователя
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
