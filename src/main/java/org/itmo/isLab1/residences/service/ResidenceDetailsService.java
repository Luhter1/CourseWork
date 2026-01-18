package org.itmo.isLab1.residences.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itmo.isLab1.residences.dto.ResidenceDetailsCreateDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsUpdateDto;
import org.itmo.isLab1.residences.dto.ValidationResponseDto;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.entity.ValidationStatus;
import org.itmo.isLab1.residences.mapper.ResidenceDetailsMapper;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidenceDetailsService {

    private final ResidenceDetailsRepository repository;
    private final ResidenceDetailsMapper mapper;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    /**
     * Создание профиля резиденции
     *
     * @param id ID профиля резиденции
     * @return профиль резиденции
     */
    @Transactional
    public ResidenceDetailsDto create(ResidenceDetailsCreateDto dto) {
        Long userId = userService.getCurrentUser().getId();

        String contactsJson;
        try {
            contactsJson = objectMapper.writeValueAsString(dto.getContacts());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Невозможно сериализовать контакты в JSON: " + e.getMessage(), e);
        }
        
        Long id = repository.createResidenceProfile(
            dto.getTitle(),
            dto.getDescription(),
            dto.getLocation(),
            contactsJson,
            userId
        );

        
        ResidenceDetails details = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден после создания"));
        return mapper.toResidenceDetailsWithValidation(details);
    }

    /**
     * Обновление профиля резиденции по ID
     *
     * @param id ID профиля резиденции
     * @return профиль резиденции
     */
    @Transactional
    public ResidenceDetailsDto update(Long id, ResidenceDetailsUpdateDto dto) {
        ResidenceDetails details = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден"));

        ValidationStatus oldStatus = details.getValidationStatus();

        mapper.updateResidenceDetails(dto, details);

        if (oldStatus == ValidationStatus.REJECTED) {
            details.setValidationStatus(ValidationStatus.PENDING);
        }

        details = repository.save(details);
        return mapper.toResidenceDetailsWithValidation(details);
    }

    /**
     * Получение профиля резиденции текущего пользователя
     *
     * @return профиль резиденции
     */
    public ResidenceDetailsDto getMyProfile() {
        User user = userService.getCurrentUser();
        ResidenceDetails details = repository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден"));
        return mapper.toResidenceDetailsWithValidation(details);
    }

    /**
     * Получение профиля резиденции по ID
     *
     * @param id ID профиля резиденции
     * @return профиль резиденции
     */
    public ResidenceDetailsDto getProfile(Long residenceId) {
        ResidenceDetails details = repository.findById(residenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции с id " + residenceId + " не найден"));
        
        if (!details.getIsPublished()) {
            throw new PolicyViolationError("Доступ к резиденции возможен только если она опубликована");
        }
        repository.createResidenceViewLog(residenceId);
        return mapper.toResidenceDetails(details);
    }

    /**
     * Получение списка всех опубликованных резиденций с пагинацией
     *
     * @param pageable параметры пагинации
     * @return страница с профилями резиденций
     */
    public Page<ResidenceDetailsDto> getAllPublished(Pageable pageable) {
        return repository.findByIsPublishedTrue(pageable)
                .map(mapper::toResidenceDetails);
    }

    /**
     * Получение статуса валидации профиля резиденции текущего пользователя
     *
     * @return статус валидации с комментарием и датой отправки
     */
    public ValidationResponseDto getMyValidationStatus() {
        User user = userService.getCurrentUser();
        ResidenceDetails details = repository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден"));
        
        return ValidationResponseDto.builder()
                .validationStatus(details.getValidationStatus())
                .validationComment(details.getValidationComment())
                .validationSubmittedAt(details.getValidationSubmittedAt())
                .build();
    }

}