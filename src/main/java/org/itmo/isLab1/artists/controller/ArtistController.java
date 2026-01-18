package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.applications.dto.ApplicationDto;
import org.itmo.isLab1.applications.service.ApplicationService;
import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
import org.itmo.isLab1.artists.service.ArtistService;
import org.itmo.isLab1.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.notifications.service.NotificationService;
import org.itmo.isLab1.users.UserService;
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
    private final ApplicationService applicationService;
    private final UserService userService;

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
        var user = userService.getCurrentUser();
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

    @PostMapping("/invite")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Long> inviteArtist(
            @Valid @RequestBody NotificationCreateDto request) {

        Long notificationId = notificationService.sendInviteNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificationId);
    }

    @GetMapping("/me/applications")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<ApplicationDto>> getMyApplications(
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ApplicationDto> page = applicationService.getMyApplications(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/me/applications/history")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<ApplicationDto>> getAllMyApplications(
            @PageableDefault(size = 20, sort = "submittedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ApplicationDto> page = applicationService.getAllMyApplications(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/me/applications/{id}/confirm")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ApplicationDto> confirmMyApplication(
            @PathVariable Long id) {

        ApplicationDto application = applicationService.confirmMyApplication(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/me/applications/{id}/decline")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ApplicationDto> declineMyApplication(
            @PathVariable Long id) {

        ApplicationDto application = applicationService.declineMyApplication(id);
        return ResponseEntity.ok(application);
    }
}