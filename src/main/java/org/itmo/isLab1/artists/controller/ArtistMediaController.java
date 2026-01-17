package org.itmo.isLab1.artists.controller;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.MediaDto;
import org.itmo.isLab1.artists.service.ArtistMediaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistMediaController {

    private final ArtistMediaService artistWorkService;

    /**
     * Получение списка медиафайлов работы для публичного доступа
     *
     * @param id       ID художника
     * @param workId   ID работы
     * @param pageable параметры пагинации
     * @return страница медиафайлов с заголовком X-Total-Count
     */
    @GetMapping("/{userId}/works/{workId}/media")
    public ResponseEntity<Page<MediaDto>> getWorkMediasPublic(
            @PathVariable Long userId,
            @PathVariable Long workId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaDto> mediaPage = artistWorkService.getArtistWorkMedias(userId, workId, pageable);
        
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(mediaPage.getTotalElements()))
                .body(mediaPage);
    }

    /**
     * Получение списка медиафайлов своих работ для авторизованных художников
     *
     * @param workId   ID работы
     * @param pageable параметры пагинации
     * @return страница медиафайлов с заголовком X-Total-Count
     */
    @GetMapping("/me/works/{workId}/media")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<MediaDto>> getWorkMediasForCurrentArtist(
            @PathVariable Long workId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaDto> mediaPage = artistWorkService.getCurrentArtistWorkMedias(workId, pageable);
        
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(mediaPage.getTotalElements()))
                .body(mediaPage);
    }

    /**
     * Загрузка медиафайлов для указанной работы
     *
     * @param workId ID работы
     * @param files  массив файлов для загрузки
     * @return список DTO загруженных медиафайлов со статусом 201 Created
     */
    @PostMapping(value = "/me/works/{workId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<MediaDto>> uploadMedia(
            @PathVariable Long workId,
            @RequestParam("files") MultipartFile[] files) {
        // Получаем ID текущего художника из контекста безопасности
        Long currentArtistId = artistWorkService.getCurrentArtistIdForController();
        List<MediaDto> uploadedMedia = artistWorkService.uploadMedia(currentArtistId, workId, files);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedMedia);
    }

    /**
     * Удаление медиафайла
     *
     * @param workId  ID работы
     * @param mediaId ID медиафайла
     * @return ответ со статусом 204 No Content
     */
    @DeleteMapping("/me/works/{workId}/media/{mediaId}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long workId,
            @PathVariable Long mediaId) {
        // Получаем ID текущего художника из контекста безопасности
        Long currentArtistId = artistWorkService.getCurrentArtistIdForController();
        artistWorkService.deleteMedia(currentArtistId, workId, mediaId);
        
        return ResponseEntity.noContent().build();
    }
}