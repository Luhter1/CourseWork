package org.itmo.isLab1.residences.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.residences.dto.ResidenceDetailsCreateDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsUpdateDto;
import org.itmo.isLab1.residences.dto.ResidenceStatsDto;
import org.itmo.isLab1.residences.dto.ValidationResponseDto;
import org.itmo.isLab1.residences.service.ResidenceDetailsService;
import org.itmo.isLab1.residences.service.ResidenceStatsService;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления профилями резиденций
 */
@RestController
@RequestMapping("/api/residences")
@RequiredArgsConstructor
public class ResidenceDetailsController {

    private final ResidenceDetailsService residenceDetailsService;
    private final UserRepository userRepository;
    private final ResidenceStatsService residenceStatsService;

    /**
     * Получение статистики резиденции для текущего пользователя
     *
     * @return DTO со статистикой резиденции
     */
    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceStatsDto> getStatsForCurrentUser() {
        ResidenceStatsDto dto = residenceStatsService.getStatsForCurrentUser();
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Создание профиля резиденции для текущего пользователя
     *
     * @param request данные для создания профиля
     * @return созданный профиль резиденции
     */
    @PostMapping("/me")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceDetailsDto> createMyProfile(@Valid @RequestBody ResidenceDetailsCreateDto request) {
        User user = getCurrentUser();
        ResidenceDetailsDto dto = residenceDetailsService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Обновление профиля резиденции текущего пользователя
     *
     * @param request данные для обновления профиля
     * @return обновленный профиль резиденции
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceDetailsDto> updateMyProfile(@Valid @RequestBody ResidenceDetailsUpdateDto request) {
        ResidenceDetailsDto currentProfile = residenceDetailsService.getMyProfile();
        ResidenceDetailsDto dto = residenceDetailsService.update(currentProfile.getId(), request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Получение профиля резиденции текущего пользователя
     *
     * @return профиль резиденции
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceDetailsDto> getMyProfile() {
        ResidenceDetailsDto dto = residenceDetailsService.getMyProfile();
        return ResponseEntity.ok(dto);
    }

    /**
     * Получение статуса валидации профиля резиденции текущего пользователя
     *
     * @return статус валидации с комментарием и датой отправки
     */
    @GetMapping("/me/validation-status")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ValidationResponseDto> getMyValidationStatus() {
        ValidationResponseDto dto = residenceDetailsService.getMyValidationStatus();
        return ResponseEntity.ok(dto);
    }

    /**
     * Получение профиля резиденции по ID
     *
     * @param id ID профиля резиденции
     * @return профиль резиденции
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidenceDetailsDto> getProfile(@PathVariable Long id) {
        ResidenceDetailsDto dto = residenceDetailsService.getProfile(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Получение списка всех опубликованных резиденций с пагинацией
     *
     * @param pageable параметры пагинации
     * @return страница с профилями резиденций
     */
    @GetMapping
    public ResponseEntity<Page<ResidenceDetailsDto>> getAllPublished(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResidenceDetailsDto> page = residenceDetailsService.getAllPublished(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из контекста безопасности
     *
     * @return пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));
        return user;
    }
}