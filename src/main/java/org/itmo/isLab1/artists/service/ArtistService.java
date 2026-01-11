package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.ArtistDetailsMapper;
import org.itmo.isLab1.artists.ArtistMapper;
import org.itmo.isLab1.artists.dto.ArtistProfileResponse;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateRequest;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.repository.ArtistDetailsRepository;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.Role;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final UserRepository userRepository;
    private final ArtistDetailsRepository artistDetailsRepository;
    private final ArtistMapper artistMapper;
    private final ArtistDetailsMapper artistDetailsMapper;

    /**
     * Получает профиль текущего художника
     *
     * @param authentication объект аутентификации
     * @return профиль художника
     */
    public ArtistProfileResponse getArtistProfile(Authentication authentication) {
        // Извлекаем пользователя по email из authentication
        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));

        // Проверяем, что пользователь является художником
        if (!Role.ROLE_ARTIST.equals(user.getRole())) {
            throw new AccessDeniedException("Доступ разрешен только для художников");
        }

        // Находим связанный ArtistDetails (может отсутствовать)
        ArtistDetails details = artistDetailsRepository.findByUser(user).orElse(null);

        // Преобразуем в DTO с помощью маппера
        return artistMapper.toProfileResponse(user, details);
    }

    /**
     * Обновляет профиль художника
     *
     * @param authentication объект аутентификации
     * @param request        данные для обновления профиля
     * @return обновленный профиль художника
     */
    @Transactional
    public ArtistProfileResponse updateArtistProfile(Authentication authentication, ArtistProfileUpdateRequest request) {
        // Извлекаем пользователя по email из authentication
        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));

        // Проверяем, что пользователь является художником
        if (!Role.ROLE_ARTIST.equals(user.getRole())) {
            throw new AccessDeniedException("Доступ разрешен только для художников");
        }

        // Обновляем данные пользователя
        artistMapper.updateUserFromRequest(request, user);
        User savedUser = userRepository.save(user);

        // Находим или создаем ArtistDetails
        ArtistDetails details = artistDetailsRepository.findByUser(savedUser)
                .orElse(null);

        if (details != null) {
            // Обновляем существующие детали
            artistDetailsMapper.updateArtistDetailsFromRequest(request, details);
        } else {
            // Создаем новые детали
            details = artistDetailsMapper.toArtistDetails(request, savedUser);
        }

        // Сохраняем ArtistDetails
        ArtistDetails savedDetails = artistDetailsRepository.save(details);

        // Возвращаем обновленный профиль
        return artistMapper.toProfileResponse(savedUser, savedDetails);
    }
}