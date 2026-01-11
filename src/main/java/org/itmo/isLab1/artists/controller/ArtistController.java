package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.ArtistProfileResponse;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateRequest;
import org.itmo.isLab1.artists.service.ArtistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления профилями художников
 */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    /**
     * Получение профиля текущего художника
     *
     * @param authentication объект аутентификации Spring Security
     * @return профиль художника
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ArtistProfileResponse getMyProfile(Authentication authentication) {
        return artistService.getArtistProfile(authentication);
    }

    /**
     * Обновление профиля текущего художника
     *
     * @param request        данные для обновления профиля (валидированные)
     * @param authentication объект аутентификации Spring Security
     * @return обновленный профиль художника
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ArtistProfileResponse updateMyProfile(
            @Valid @RequestBody ArtistProfileUpdateRequest request,
            Authentication authentication) {
        return artistService.updateArtistProfile(authentication, request);
    }
}