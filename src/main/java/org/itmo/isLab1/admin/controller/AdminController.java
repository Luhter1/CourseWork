package org.itmo.isLab1.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.admin.service.AdminService;
import org.itmo.isLab1.residences.dto.ResidenceDetailsDto;
import org.itmo.isLab1.residences.dto.ValidationActionDto;
import org.itmo.isLab1.residences.dto.ValidationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления заявками на валидацию профилей резиденций суперадмином
 */
@RestController
@RequestMapping("/api/admin/validation-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * Получение списка заявок на валидацию с пагинацией
     *
     * @param pageable параметры пагинации
     * @return страница с заявками на валидацию
     */
    @GetMapping
    public ResponseEntity<Page<ResidenceDetailsDto>> getValidationRequests(
            @PageableDefault(size = 20, sort = "validationSubmittedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResidenceDetailsDto> page = adminService.getPageOfValidationRequests(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Просмотр детальной информации по конкретной заявке на валидацию
     *
     * @param id ID заявки (резиденции)
     * @return детальная информация о заявке
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidenceDetailsDto> getValidationRequestDetails(@PathVariable Long id) {
        ResidenceDetailsDto dto = adminService.getValidationRequestDetails(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Одобрение заявки на валидацию
     *
     * @param id ID заявки (резиденции)
     * @return статус валидации после одобрения
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ValidationResponseDto> approveValidationRequest(@PathVariable Long id) {
        ResidenceDetailsDto dto = adminService.approveValidationRequest(id);
        return ResponseEntity.ok(dto.getValidation());
    }

    /**
     * Отклонение заявки на валидацию с указанием причины
     *
     * @param id ID заявки (резиденции)
     * @param request данные с причиной отклонения
     * @return статус валидации после отклонения
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ValidationResponseDto> rejectValidationRequest(
            @PathVariable Long id,
            @RequestBody @Valid ValidationActionDto request) {
        ResidenceDetailsDto dto = adminService.rejectValidationRequest(id, request.getComment());
        return ResponseEntity.ok(dto.getValidation());
    }
}
