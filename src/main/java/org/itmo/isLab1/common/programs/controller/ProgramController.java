package org.itmo.isLab1.common.programs.controller;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.common.programs.dto.*;
import org.itmo.isLab1.common.programs.service.ProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService; 

    /**
     * Возвращает пагинированный список программ
     *
     * @param pageable параметры пагинации
     * @return страница с программами резиденции
     */
    @GetMapping
    public ResponseEntity<Page<ResidenceProgramPreviewDto>> getPrograms(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResidenceProgramPreviewDto> page = programService.getPrograms(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Возвращает программу по id
     *
     * @param pageable параметры пагинации
     * @return страница с программами резиденции
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidenceProgramDto> getProgramById(
            @PathVariable Long id) {
        ResidenceProgramDto program = programService.getProgramById(id);
        return ResponseEntity.ok(program);
    }
}
