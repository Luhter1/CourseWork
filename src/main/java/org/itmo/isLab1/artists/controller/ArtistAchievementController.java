package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.service.ArtistAchievementService;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления достижениями текущего художника
 */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistAchievementController {

    private final ArtistAchievementService artistAchievementService;

    /**
     * Получение списка достижений художника
     *
     * @return список достижений
     */
    @GetMapping("/{id}/achievements")
    public ResponseEntity<Page<AchievementDto>> getArtistAchievements(
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        @PathVariable Long userId
    ) {
        var objs = artistAchievementService.getArtistAchievements(userId, pageable);

        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(objs.getTotalElements()))
            .body(objs);
    }

    /**
     * Получение списка достижений текущего художника
     *
     * @return список достижений
     */
    @GetMapping("/me/achievements")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<AchievementDto>> getMyAchievements(
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        var objs = artistAchievementService.getCurrentArtistAchievements(pageable);
        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(objs.getTotalElements()))
            .body(objs);
    }

    /**
     * Создание нового достижения для текущего художника
     *
     * @param createDto      данные для создания достижения (валидированные)
     * @return созданное достижение
     */
    @PostMapping("/me/achievements")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<AchievementDto> createAchievement(
            @Valid @RequestBody AchievementCreateDto createDto) {
        var obj = artistAchievementService.createAchievement(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(obj);
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
    public ResponseEntity<AchievementDto> updateAchievement(
            @PathVariable Long achievementId,
            @Valid @RequestBody AchievementUpdateDto updateDto) {
        var obj = artistAchievementService.updateAchievement(achievementId, updateDto);

        return ResponseEntity.ok(obj);
    }

    /**
     * Удаление достижения текущего художника
     *
     * @param id             ID достижения
     */
    @DeleteMapping("/me/achievements/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Void> deleteAchievement(
            @PathVariable Long achievementId) {
        artistAchievementService.deleteAchievement(achievementId);

        return ResponseEntity.noContent().build();
    }
}
