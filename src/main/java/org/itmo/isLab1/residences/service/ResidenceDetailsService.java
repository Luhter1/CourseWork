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
import org.springframework.dao.DataAccessException;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidenceDetailsService {

    private final ResidenceDetailsRepository repository;
    private final ResidenceDetailsMapper mapper;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    /**
     * Создание профиля резиденции
     *
     * @param id ID профиля резиденции
     * @return профиль резиденции
     */
    @Transactional
    public ResidenceDetailsDto create(ResidenceDetailsCreateDto dto) {
        Long userId = getCurrentUser().getId();

        String contactsJson;
        try {
            contactsJson = objectMapper.writeValueAsString(dto.getContacts());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Невозможно сериализовать контакты в JSON: " + e.getMessage(), e);
        }
        
        Long id;
        try {
            id = repository.createResidenceProfile(
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getLocation(),
                    contactsJson,
                    userId
            );
        } catch (DataAccessException e) {
            String errorMessage = extractDatabaseErrorMessage(e);
            throw new IllegalStateException("Не удалось создать профиль резиденции: " + errorMessage, e);
        }
        
        ResidenceDetails details = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден после создания"));
        return mapper.toResidenceDetailsWithValidation(details);
    }
    
    private String extractDatabaseErrorMessage(DataAccessException e) {
        Throwable cause = e.getRootCause();
        if (cause != null && cause.getMessage() != null) {
            String message = cause.getMessage();
            // Извлекаем сообщение после "ERROR:" или берем все сообщение
            if (message.contains("ERROR:")) {
                int startIndex = message.indexOf("ERROR:") + 6;
                int endIndex = message.indexOf("\n", startIndex);
                if (endIndex > startIndex) {
                    return message.substring(startIndex, endIndex).trim();
                }
            }
            return message;
        }
        return e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка базы данных";
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
        User user = getCurrentUser();
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
        User user = getCurrentUser();
        ResidenceDetails details = repository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции не найден"));
        
        return ValidationResponseDto.builder()
                .validationStatus(details.getValidationStatus())
                .validationComment(details.getValidationComment())
                .validationSubmittedAt(details.getValidationSubmittedAt())
                .build();
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из контекста безопасности
     *
     * @return пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));
        return user;
    }
}