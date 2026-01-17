package org.itmo.isLab1.common.notifications.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;

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
}
