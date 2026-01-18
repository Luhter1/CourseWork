package org.itmo.isLab1.programs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.applications.dto.ApplicationCreateDto;
import org.itmo.isLab1.applications.service.ApplicationService;
import org.itmo.isLab1.programs.dto.*;
import org.itmo.isLab1.programs.service.ProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService; 
    private final ApplicationService applicationService; 

    /**
     * Возвращает пагинированный список программ
     *
     * @param pageable параметры пагинации
     * @return страница с программами резиденции
     */
    @GetMapping
    public ResponseEntity<Page<ProgramPreviewDto>> getPrograms(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProgramPreviewDto> page = programService.getPrograms(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Возвращает программу по id
     *
     * @param pageable параметры пагинации
     * @return страница с программами резиденции
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgramById(
            @PathVariable Long id) {
        ProgramDto program = programService.getProgramById(id);
        return ResponseEntity.ok(program);
    }


    @PostMapping("/{programId}/application")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Long> createApplication(
            @PathVariable Long programId,
            @Valid @RequestBody ApplicationCreateDto application) {

        Long applicationId = applicationService.createApplication(programId, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationId);
    }
}
