package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.MediaDto;
import org.itmo.isLab1.artists.dto.WorkCreateDto;
import org.itmo.isLab1.artists.dto.WorkDto;
import org.itmo.isLab1.artists.dto.WorkUpdateDto;
import org.itmo.isLab1.artists.service.ArtistWorkService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST-контроллер для управления работами текущего художника
 */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistWorkController {

    private final ArtistWorkService artistWorkService;

    /**
     * Получение списка работ художника
     *
     * @param pageable параметры пагинации
     * @return страница работ
     */
    @GetMapping("/{id}/works")
    public ResponseEntity<Page<WorkDto>> getWorks(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var works = artistWorkService.getWorksForArtist(pageable, id);
        
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(works.getTotalElements()))
                .body(works);
    }

    /**
     * Получение списка работ текущего художника
     *
     * @param pageable параметры пагинации
     * @return страница работ
     */
    @GetMapping("/me/works")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Page<WorkDto>> getWorksForCurrentArtist(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var works = artistWorkService.getWorksForCurrentArtist(pageable);
        
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(works.getTotalElements()))
                .body(works);
    }

    /**
     * Создание новой работы для текущего художника
     *
     * @param request данные для создания работы (валидированные)
     * @return созданная работа
     */
    @PostMapping("/me/works")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<WorkDto> createWork(@Valid @RequestBody WorkCreateDto request) {
        var work = artistWorkService.createWork(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(work);
    }

    /**
     * Обновление существующей работы текущего художника
     *
     * @param id      ID работы
     * @param request данные для обновления (валидированные)
     * @return обновленная работа
     */
    @PutMapping("/me/works/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<WorkDto> updateWork(
            @PathVariable Long id,
            @Valid @RequestBody WorkUpdateDto request) {
        var work = artistWorkService.updateWork(id, request);
        
        return ResponseEntity.ok(work);
    }

    /**
     * Удаление работы текущего художника
     *
     * @param id ID работы
     */
    @DeleteMapping("/me/works/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        artistWorkService.deleteWork(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение списка медиафайлов работы для публичного доступа
     *
     * @param id       ID художника
     * @param workId   ID работы
     * @param pageable параметры пагинации
     * @return страница медиафайлов с заголовком X-Total-Count
     */
    @GetMapping("/{id}/works/{workId}/media")
    public ResponseEntity<Page<MediaDto>> getWorkMediasPublic(
            @PathVariable Long id,
            @PathVariable Long workId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaDto> mediaPage = artistWorkService.getWorkMedias(id, workId, pageable);
        
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
        // Получаем ID текущего художника из контекста безопасности
        Long currentArtistId = artistWorkService.getCurrentArtistIdForController();
        Page<MediaDto> mediaPage = artistWorkService.getWorkMedias(currentArtistId, workId, pageable);
        
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