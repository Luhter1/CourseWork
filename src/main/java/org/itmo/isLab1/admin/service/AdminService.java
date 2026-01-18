package org.itmo.isLab1.admin.service;

import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.notifications.entity.NotificationCategory;
import org.itmo.isLab1.common.notifications.service.NotificationService;
import org.itmo.isLab1.residences.dto.ResidenceDetailsDto;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.entity.ValidationStatus;
import org.itmo.isLab1.residences.mapper.ResidenceDetailsMapper;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ResidenceDetailsRepository repository;
    private final ResidenceDetailsMapper mapper;
    private final NotificationService notificationService;
    
    /**
     * Получение страницы заявок на валидацию профилей резиденций со статусом PENDING
     *
     * @param pageable параметры пагинации
     * @return страница с заявками на валидацию
     */
    @Transactional(readOnly = true)
    public Page<ResidenceDetailsDto> getPageOfValidationRequests(Pageable pageable) {
        return repository.findByValidationStatus(ValidationStatus.PENDING, pageable)
                .map(mapper::toResidenceDetailsWithValidation);
    }

    /**
     * Получение детальной информации по конкретной заявке на валидацию
     *
     * @param residenceId ID профиля резиденции
     * @return детальная информация о заявке
     * @throws ResourceNotFoundException если резиденция не найдена
     */
    @Transactional(readOnly = true)
    public ResidenceDetailsDto getValidationRequestDetails(Long residenceId) {
        ResidenceDetails details = repository.findById(residenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции с id " + residenceId + " не найден"));
        return mapper.toResidenceDetailsWithValidation(details);
    }

    /**
     * Одобрение заявки на валидацию профиля резиденции
     *
     * @param residenceId ID профиля резиденции
     * @return обновленная информация о профиле
     * @throws ResourceNotFoundException если резиденция не найдена
     */
    @Transactional
    public ResidenceDetailsDto approveValidationRequest(Long residenceId) {
        ResidenceDetails details = repository.findById(residenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции с id " + residenceId + " не найден"));
        
        details.setValidationStatus(ValidationStatus.APPROVED);
        details.setValidationComment(null);
        
        details = repository.save(details);

        notificationService.sendNotification(
                details.getUser().getUsername(),
                String.format("Ваша заявка на валидацию резиденции \"%s\" была одобрена.",
                    details.getTitle()),
                NotificationCategory.STATUS,
                null
            );
        return mapper.toResidenceDetailsWithValidation(details);
    }
    
    /**
     * Отклонение заявки на валидацию профиля резиденции с указанием причины
     *
     * @param residenceId ID профиля резиденции
     * @param comment причина отклонения
     * @return обновленная информация о профиле
     * @throws ResourceNotFoundException если резиденция не найдена
     */
    @Transactional
    public ResidenceDetailsDto rejectValidationRequest(Long residenceId, String comment) {
        ResidenceDetails details = repository.findById(residenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции с id " + residenceId + " не найден"));
        
        details.setValidationStatus(ValidationStatus.REJECTED);
        details.setValidationComment(comment);
        
        details = repository.save(details);

        notificationService.sendNotification(
                details.getUser().getUsername(),
                String.format("Ваша заявка на валидацию резиденции \"%s\" была отклонена.",
                    details.getTitle()),
                NotificationCategory.STATUS,
                null
            );
        return mapper.toResidenceDetailsWithValidation(details);
    }
}
