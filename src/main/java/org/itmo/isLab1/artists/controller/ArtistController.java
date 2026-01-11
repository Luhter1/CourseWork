package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.ArtistProfileResponse;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateRequest;
import org.itmo.isLab1.artists.service.ArtistService;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления профилями художников
 */
@RestController
@RequestMapping("/api/artists/me")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final UserRepository userRepository;

    /**
     * Получение профиля текущего художника
     *
     * @param authentication объект аутентификации Spring Security
     * @return профиль художника
     */
    @GetMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ArtistProfileResponse getMyProfile() {
        var user = getCurrentUser();
        return artistService.getArtistProfile(user);
    }

    /**
     * Обновление профиля текущего художника
     *
     * @param request        данные для обновления профиля (валидированные)
     * @param authentication объект аутентификации Spring Security
     * @return обновленный профиль художника
     */
    @PutMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ArtistProfileResponse updateMyProfile(@Valid @RequestBody ArtistProfileUpdateRequest request) {
        var user = getCurrentUser();
        return artistService.updateArtistProfile(user, request);
    }

    /**
     * Вспомогательный метод для получения ID текущего художника из контекста безопасности
     *
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException     если пользователь не является художником
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getPrincipal().toString();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        return user;
    }
}