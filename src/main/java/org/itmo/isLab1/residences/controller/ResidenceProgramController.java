package org.itmo.isLab1.residences.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.common.programs.dto.ProgramStatsDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramUpdateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramPreviewDto;
import org.itmo.isLab1.residences.service.ResidenceProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления программами резиденций
 */
@RestController
@RequestMapping("/api/residences/me/programs")
@RequiredArgsConstructor
public class ResidenceProgramController {

    private final ResidenceProgramService residenceProgramService;

    /**
     * Возвращает пагинированный список программ текущего пользователя
     *
     * @param pageable параметры пагинации
     * @return страница с программами резиденции
     */
    @GetMapping
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Page<ResidenceProgramPreviewDto>> getPrograms(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResidenceProgramPreviewDto> page = residenceProgramService.getProgramsByResidenceId(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Возвращает программу по идентификатору
     *
     * @param id идентификатор программы
     * @return программа резиденции
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceProgramDto> getProgramById(@PathVariable Long id) {
        ResidenceProgramDto dto = residenceProgramService.getProgramById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Создаёт новую программу для резиденции текущего пользователя
     *
     * @param createDto данные для создания программы
     * @return созданная программа
     */
    @PostMapping
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceProgramDto> createProgram(@Valid @RequestBody ResidenceProgramCreateDto createDto) {
        ResidenceProgramDto dto = residenceProgramService.createProgram(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Создаёт новую программу для резиденции текущего пользователя
     *
     * @param createDto данные для создания программы
     * @return созданная программа
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ResidenceProgramDto> updateProgram(
                @PathVariable Long id,
                @Valid @RequestBody ResidenceProgramUpdateDto updateDto) {
        ResidenceProgramDto dto = residenceProgramService.updateProgram(id, updateDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dto);
    }

    /**
     * Публикует программу для резиденции текущего пользователя
     *
     * @param createDto данные для создания программы
     * @return созданная программа
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Void> publishProgram(
                @PathVariable Long id) {
        residenceProgramService.publishProgram(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Разпубликует программу для резиденции текущего пользователя
     *
     * @param createDto данные для создания программы
     * @return созданная программа
     */
    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<Void> unpublishProgram(
                @PathVariable Long id) {
        residenceProgramService.unpublishProgram(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Возвращает программу по идентификатору
     *
     * @param id идентификатор программы
     * @return программа резиденции
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('RESIDENCE_ADMIN')")
    public ResponseEntity<ProgramStatsDto> getProgramStats(@PathVariable Long id) {
        ProgramStatsDto dto = residenceProgramService.getProgramStatsById(id);
        return ResponseEntity.ok(dto);
    }
}
