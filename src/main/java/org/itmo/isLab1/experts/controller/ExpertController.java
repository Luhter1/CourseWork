package org.itmo.isLab1.experts.controller;

import org.itmo.isLab1.experts.service.ExpertService;
import org.itmo.isLab1.experts.dto.ExpertDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;
    
    /**
     * Получение профилей экспертов
     *
     * @return профили экспертов
     */
    @GetMapping
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Page<ExpertDto>> getExpertsProfile(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var obj = expertService.getExpertsProfile(pageable);

        return ResponseEntity.ok(obj);
    }

    /**
     * Назначить эксперта на программу
     *
     * @return профили экспертов
     */
    @PostMapping("{id}/programs/{programId}")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Page<ExpertDto>> setExpertToProgram(
            @PathVariable Long id,
            @PathVariable Long programId) {
        expertService.setExpertToProgram(id, programId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // отозвать с программы
    // отказаться, если нет оценок
    // получить список неоцененных заявок
    // оценить заявку
}
