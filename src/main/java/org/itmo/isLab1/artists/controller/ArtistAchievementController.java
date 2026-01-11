package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementResponseDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.repository.ArtistDetailsRepository;
import org.itmo.isLab1.artists.service.ArtistAchievementService;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.Role;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления достижениями текущего художника
 */
@RestController
@RequestMapping("/api/artists/me/achievements")
@RequiredArgsConstructor
public class ArtistAchievementController {

    private final ArtistAchievementService artistAchievementService;
    private final UserRepository userRepository;
    private final ArtistDetailsRepository artistDetailsRepository;

    /**
     * Получение списка достижений текущего художника
     *
     * @param authentication объект аутентификации Spring Security
     * @return список достижений
     */
    @GetMapping
    @PreAuthorize("hasRole('ARTIST')")
    public List<AchievementResponseDto> getMyAchievements(Authentication authentication) {
        Long artistId = getCurrentArtistId(authentication);
        return artistAchievementService.getArtistAchievements(artistId);
    }

    /**
     * Создание нового достижения для текущего художника
     *
     * @param createDto      данные для создания достижения (валидированные)
     * @param authentication объект аутентификации Spring Security
     * @return созданное достижение
     */
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public AchievementResponseDto createAchievement(
            @RequestBody @Valid AchievementCreateDto createDto,
            Authentication authentication) {
        Long artistId = getCurrentArtistId(authentication);
        return artistAchievementService.createAchievement(artistId, createDto);
    }

    /**
     * Обновление существующего достижения текущего художника
     *
     * @param id             ID достижения
     * @param updateDto      данные для обновления (валидированные)
     * @param authentication объект аутентификации Spring Security
     * @return обновленное достижение
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public AchievementResponseDto updateAchievement(
            @PathVariable Long id,
            @RequestBody @Valid AchievementUpdateDto updateDto,
            Authentication authentication) {
        Long artistId = getCurrentArtistId(authentication);
        return artistAchievementService.updateAchievement(artistId, id, updateDto);
    }

    /**
     * Удаление достижения текущего художника
     *
     * @param id             ID достижения
     * @param authentication объект аутентификации Spring Security
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deleteAchievement(
            @PathVariable Long id,
            Authentication authentication) {
        Long artistId = getCurrentArtistId(authentication);
        artistAchievementService.deleteAchievement(artistId, id);
    }

    /**
     * Вспомогательный метод для получения ID текущего художника из контекста безопасности
     *
     * @param authentication объект аутентификации
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException     если пользователь не является художником
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    private Long getCurrentArtistId(Authentication authentication) {
        // Извлекаем пользователя по email из authentication
        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));

        // Проверяем, что пользователь является художником
        if (!Role.ROLE_ARTIST.equals(user.getRole())) {
            throw new AccessDeniedException("Доступ разрешен только для художников");
        }

        // Находим связанный ArtistDetails
        ArtistDetails artistDetails = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));

        return artistDetails.getId();
    }
}
