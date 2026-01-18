package org.itmo.isLab1.reviews.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.reviews.dto.ReviewCreateDto;
import org.itmo.isLab1.reviews.dto.ReviewDto;
import org.itmo.isLab1.reviews.dto.ReviewUpdateDto;
import org.itmo.isLab1.reviews.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления отзывами на программы резиденций.
 * Предоставляет REST API для получения списка отзывов и создания новых отзывов.
 */
@RestController
@RequestMapping("/api/programs/{programId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Получает пагинированный список отзывов на программу.
     *
     * @param programId идентификатор программы
     * @param pageable параметры пагинации (по умолчанию размер страницы 10)
     * @return страница с отзывами
     */
    @GetMapping
    public ResponseEntity<Page<ReviewDto>> getReviews(
            @PathVariable Long programId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReviewDto> reviews = reviewService.getReviewsByProgramId(programId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Создает новый отзыв на программу.
     * Доступно только для пользователей с ролью ARTIST.
     *
     * @param programId идентификатор программы
     * @param createDto данные для создания отзыва
     * @param user текущий аутентифицированный пользователь
     * @return созданный отзыв со статусом 201 CREATED
     */
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable Long programId,
            @Valid @RequestBody ReviewCreateDto createDto) {
        ReviewDto review = reviewService.createReview(programId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    /**
     * Обновляет отзыв на программу.
     * Доступно только для пользователей с ролью ARTIST.
     *
     * @param programId идентификатор программы
     * @param createDto данные для создания отзыва
     * @param user текущий аутентифицированный пользователь
     * @return созданный отзыв со статусом 201 CREATED
     */
    @PutMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long programId,
            @Valid @RequestBody ReviewUpdateDto updateDto) {
        ReviewDto review = reviewService.updateReview(programId, updateDto);

        return ResponseEntity.ok(review);
    }
}
