package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
import org.itmo.isLab1.artists.service.ArtistService;
import org.itmo.isLab1.common.applications.dto.ArtistApplicationDto;
import org.itmo.isLab1.common.notifications.dto.NotificationsDto;
import org.itmo.isLab1.common.notifications.service.NotificationService;
import org.itmo.isLab1.common.applications.service.ArtistApplicationService;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * REST-контроллер для управления профилями художников
 */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final NotificationService notificationService;
    private final ArtistApplicationService artistApplicationService;
    private final UserRepository userRepository;

    /**
     * Получение профилей художников
     *
     * @return профили художников
     */
    @GetMapping
    public ResponseEntity<Page<ArtistProfileDto>> getArtistsProfile(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var obj = artistService.getArtistsProfile(pageable);

        return ResponseEntity.ok(obj);
    }

    /**
     * Получение профиля художника
     *
     * @return профиль художника
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtistProfileDto> getArtistProfile(
            @PathVariable Long id) {
        var obj = artistService.getArtistProfile(id);

        return ResponseEntity.ok(obj);
    }

    /**
     * Получение профиля текущего художника
     *
     * @return профиль художника
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistProfileDto> getMyProfile() {
        var user = getCurrentUser();
        var obj = artistService.getArtistProfile(user);

        return ResponseEntity.ok(obj);
    }

    /**
     * Создание профиля художника
     *
     * @param request        данные для создания профиля
     * @return обновленный профиль художника
     */
    @PostMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistProfileDto> createMyProfile(@Valid @RequestBody ArtistProfileCreateDto request) {
        var obj = artistService.createArtistProfile(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(obj);
    }

    /**
     * Обновление профиля текущего художника
     *
     * @param request        данные для обновления профиля (валидированные)
     * @return обновленный профиль художника
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistProfileDto> updateMyProfile(@Valid @RequestBody ArtistProfileUpdateDto request) {
        var obj = artistService.updateArtistProfile(request);
        
        return ResponseEntity.ok(obj);
    }

    @PostMapping("/{id}/invite")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Long> inviteArtist(
            @PathVariable Long id,
            @Valid @RequestBody NotificationsDto request) {

        Long notificationId = notificationService.sendInviteNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificationId);
    }

    @GetMapping("/me/applications")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<ArtistApplicationDto>> getMyApplications(
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ArtistApplicationDto> page = artistApplicationService.getMyApplications(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из контекста безопасности
     *
     * @return ID пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        return user;
    }
}