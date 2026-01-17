package org.itmo.isLab1.residences.controller;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.residences.dto.ResidenceStatsDto;
import org.itmo.isLab1.residences.service.ResidenceStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления статистикой резиденций
 */
@RestController
@RequestMapping("/api/residences")
@RequiredArgsConstructor
public class ResidenceStatsController {

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
}
