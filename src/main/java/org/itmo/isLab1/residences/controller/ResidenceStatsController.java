package org.itmo.isLab1.residences.controller;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.residences.dto.ResidenceStatsDto;
import org.itmo.isLab1.residences.service.ResidenceStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления профилями резиденций
 */
@RestController
@RequestMapping("/api/residences/me/stats")
@RequiredArgsConstructor
public class ResidenceStatsController {

    private final ResidenceStatsService residenceStatsService;

    /**
     * Получение статистики резиденции для текущего пользователя
     *
     * @return DTO со статистикой резиденции
     */
    @GetMapping
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceStatsDto> getStatsForCurrentUser() {
        ResidenceStatsDto dto = residenceStatsService.getStatsForCurrentUser();
        return ResponseEntity.ok(dto);
    }
}