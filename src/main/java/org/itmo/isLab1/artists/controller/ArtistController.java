package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
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
    private final UserRepository userRepository;

    /**
     * Получение профиля художника
     *
     * @return профиль художника
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtistProfileDto> getArtistProfile(
            @PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id " + id + " не найден"));
        var obj = artistService.getArtistProfile(user);

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
        var user = getCurrentUser();
        var obj = artistService.createArtistProfile(user, request);
        
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
        var user = getCurrentUser();
        var obj = artistService.updateArtistProfile(user, request);
        
        return ResponseEntity.ok(obj);
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
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        return user;
    }
}