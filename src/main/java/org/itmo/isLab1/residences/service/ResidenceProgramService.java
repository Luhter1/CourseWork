package org.itmo.isLab1.residences.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramUpdateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramPreviewDto;
import org.itmo.isLab1.common.programs.entity.ResidenceProgram;
import org.itmo.isLab1.common.programs.mapper.ResidenceProgramMapper;
import org.itmo.isLab1.common.programs.repository.ResidenceProgramRepository;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ResidenceProgramService {

    private final ObjectMapper objectMapper;
    private final ResidenceProgramRepository residenceProgramRepository;
    private final ResidenceProgramMapper residenceProgramMapper;
    private final UserRepository userRepository;
    private final ResidenceDetailsRepository residenceDetailsRepository;

    /**
     * Возвращает пагинированный список программ для резиденции текущего пользователя
     *
     * @param pageable    параметры пагинации
     * @return страница с программами резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public Page<ResidenceProgramPreviewDto> getProgramsByResidenceId(Pageable pageable) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        return residenceProgramRepository.findByResidenceId(residence.getId(), pageable)
            .map(residenceProgramMapper::toPreviewDto);
    }

    /**
     * Возвращает программу по идентификатору, проверяя принадлежность к резиденции
     *
     * @param residenceId идентификатор резиденции
     * @param id          идентификатор программы
     * @return DTO программы
     * @throws ResourceNotFoundException если программа или резиденция не найдены
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public ResidenceProgramDto getProgramById(Long programId) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        // Проверяем принадлежность программы к резиденции
        ResidenceProgram program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));

        return residenceProgramMapper.toDto(program);
    }


    @Transactional
    public ResidenceProgramDto publishProgram(Long programId) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        ResidenceProgram program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("У вам нет программы с id " + programId));

        program.setIsPublished(true);

        residenceProgramRepository.save(program);

        return residenceProgramMapper.toDto(program);
    }

    @Transactional
    public ResidenceProgramDto unpublishProgram(Long programId) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        ResidenceProgram program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("У вам нет программы с id " + programId));

        program.setIsPublished(false);

        residenceProgramRepository.save(program);

        return residenceProgramMapper.toDto(program);
    }
    
    /**
     * Обновляет программу
     *
     * @param updateDto
     * @return DTO созданной программы
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     * @throws IllegalArgumentException    при ошибках валидации данных
     */
    @Transactional
    public ResidenceProgramDto updateProgram(Long programId, ResidenceProgramUpdateDto updateDto) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        ResidenceProgram program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("У вам нет программы с id " + programId));

        residenceProgramMapper.updateEntity(updateDto, program);

        residenceProgramRepository.save(program);

        return residenceProgramMapper.toDto(program);
    }

    /**
     * Создаёт новую программу, используя функцию БД create_program
     *
     * @param createDto DTO с данными для создания программы
     * @return DTO созданной программы
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     * @throws IllegalArgumentException    при ошибках валидации данных
     */
    @Transactional
    public ResidenceProgramDto createProgram(ResidenceProgramCreateDto createDto) {

        User currentUser = getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        String goals, conditions;
        try {
            goals = objectMapper.writeValueAsString(createDto.getGoals());
            conditions = objectMapper.writeValueAsString(createDto.getConditions());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Невозможно сериализовать цели или условия в JSON: " + e.getMessage(), e);
        }
        
        Long programId;
        try {
            programId = residenceProgramRepository.createProgram(
                    residence.getId(),
                    createDto.getTitle(),
                    createDto.getDescription(),
                    goals,
                    conditions,
                    createDto.getDeadlineApply(),
                    createDto.getDeadlineReview(),
                    createDto.getDeadlineNotify(),
                    createDto.getDurationDays(),
                    createDto.getBudgetQuota(),
                    createDto.getPeopleQuota(),
                    currentUser.getId()
            );
        } catch (DataAccessException e) {
            String errorMessage = extractDatabaseErrorMessage(e);
            throw new IllegalArgumentException("Не удалось создать программу: " + errorMessage, e);
        }

        // Получаем созданную программу
        ResidenceProgram program = residenceProgramRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа не найдена после создания с id " + programId));

        return residenceProgramMapper.toDto(program);
    }

    /**
     * Извлекает сообщение об ошибке из исключения доступа к данным
     *
     * @param e исключение DataAccessException
     * @return сообщение об ошибке
     */
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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        return user;
    }
}
