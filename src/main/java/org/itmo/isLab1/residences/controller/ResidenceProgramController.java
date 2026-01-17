package org.itmo.isLab1.residences.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.common.programs.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramUpdateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramDto;
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
    public ResponseEntity<Page<ResidenceProgramDto>> getPrograms(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResidenceProgramDto> page = residenceProgramService.getProgramsByResidenceId(pageable);
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
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
