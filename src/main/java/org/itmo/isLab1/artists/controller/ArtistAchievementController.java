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
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistAchievementController {

    private final ArtistAchievementService artistAchievementService;
    private final UserRepository userRepository;
    private final ArtistDetailsRepository artistDetailsRepository;

    /**
     * Получение списка достижений художника
     *
     * @return список достижений
     */
    @GetMapping("/{id}/achievements")
    public List<AchievementResponseDto> getArtistAchievements(@PathVariable Long id) {
        return artistAchievementService.getArtistAchievements(id);
    }

    /**
     * Получение списка достижений текущего художника
     *
     * @return список достижений
     */
    @GetMapping("/me/achievements")
    @PreAuthorize("hasRole('ARTIST')")
    public List<AchievementResponseDto> getMyAchievements() {
        Long artistId = getCurrentArtistId();
        return artistAchievementService.getArtistAchievements(artistId);
    }

    /**
     * Создание нового достижения для текущего художника
     *
     * @param createDto      данные для создания достижения (валидированные)
     * @return созданное достижение
     */
    @PostMapping("/me/achievements")
    @PreAuthorize("hasRole('ARTIST')")
    public AchievementResponseDto createAchievement(
            @Valid AchievementCreateDto createDto) {
        Long artistId = getCurrentArtistId();
        return artistAchievementService.createAchievement(artistId, createDto);
    }

    /**
     * Обновление существующего достижения текущего художника
     *
     * @param id             ID достижения
     * @param updateDto      данные для обновления (валидированные)
     * @return обновленное достижение
     */
    @PutMapping("/me/achievements/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public AchievementResponseDto updateAchievement(
            @PathVariable Long id,
            @Valid AchievementUpdateDto updateDto) {
        Long artistId = getCurrentArtistId();
        return artistAchievementService.updateAchievement(artistId, id, updateDto);
    }

    /**
     * Удаление достижения текущего художника
     *
     * @param id             ID достижения
     */
    @DeleteMapping("/me/achievements/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deleteAchievement(
            @PathVariable Long id) {
        Long artistId = getCurrentArtistId();
        artistAchievementService.deleteAchievement(artistId, id);
    }

    /**
     * Вспомогательный метод для получения ID текущего художника из контекста безопасности
     *
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException     если пользователь не является художником
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    private Long getCurrentArtistId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getPrincipal().toString();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

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
