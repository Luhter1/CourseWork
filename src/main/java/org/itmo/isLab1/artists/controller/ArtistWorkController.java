package org.itmo.isLab1.artists.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.WorkRequest;
import org.itmo.isLab1.artists.dto.WorkResponse;
import org.itmo.isLab1.artists.service.ArtistWorkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<WorkResponse>> getWorks(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var works = artistWorkService.getWorksForArtist(pageable);
        
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
    public ResponseEntity<Page<WorkResponse>> getWorksForCurrentArtist(
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
    public ResponseEntity<WorkResponse> createWork(@Valid @RequestBody WorkRequest request) {
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
    public ResponseEntity<WorkResponse> updateWork(
            @PathVariable Long id,
            @Valid @RequestBody WorkRequest request) {
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
}