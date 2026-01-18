package org.itmo.isLab1.reviews.service;

import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import org.itmo.isLab1.common.errors.EntityDuplicateException;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.programs.repository.ProgramRepository;
import org.itmo.isLab1.reviews.dto.ReviewCreateDto;
import org.itmo.isLab1.reviews.dto.ReviewDto;
import org.itmo.isLab1.reviews.dto.ReviewUpdateDto;
import org.itmo.isLab1.reviews.entity.Review;
import org.itmo.isLab1.reviews.mapper.ReviewMapper;
import org.itmo.isLab1.reviews.repository.ReviewRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ProgramRepository programRepository;
    private final UserService userService;

    /**
     * Получает пагинированный список отзывов на программу.
     *
     * @param programId идентификатор программы
     * @param pageable параметры пагинации
     * @return страница с отзывами
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByProgramId(Long programId, Pageable pageable) {
        return reviewRepository.findByProgramId(programId, pageable)
                .map(reviewMapper::toDto);
    }

    /**
     * Создает новый отзыв на программу.
     *
     * @param programId идентификатор программы
     * @param createDto данные для создания отзыва
     * @return созданный отзыв
     * @throws ResourceNotFoundException если программа или художник не найдены
     * @throws EntityDuplicateException если отзыв уже существует
     */
    @Transactional
    public ReviewDto createReview(Long programId, ReviewCreateDto createDto) {
        User artist = userService.getCurrentUser();

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Программа с id " + programId + " не найдена"));

        if (reviewRepository.findByProgramIdAndArtistId(programId, artist.getId()).isPresent()) {
            throw new EntityDuplicateException(
                    "Отзыв от художника " + artist.getId() + " на программу " + programId + " уже существует");
        }

        Review review = reviewMapper.toEntity(createDto, artist, program);
        Review savedReview = reviewRepository.save(review);
        savedReview.setCreatedAt(ZonedDateTime.now());

        return reviewMapper.toDto(savedReview);
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param reviewId идентификатор отзыва
     * @param updateDto данные для обновления
     * @param artistId идентификатор художника, выполняющего обновление
     * @throws ResourceNotFoundException если отзыв не найден
     * @throws PolicyViolationError если художник не является владельцем отзыва
     */
    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewUpdateDto updateDto) {
        Long artistId = userService.getCurrentUser().getId();
        
        // Проверка существования отзыва
        Review review = reviewRepository.findByProgramIdAndArtistId(reviewId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв не найден"));

        // Обновление отзыва
        reviewMapper.updateEntity(review, updateDto);
        Review newReview = reviewRepository.save(review);

        return reviewMapper.toDto(newReview);
    }
}
