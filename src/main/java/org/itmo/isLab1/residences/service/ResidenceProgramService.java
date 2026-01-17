package org.itmo.isLab1.residences.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.residences.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.residences.dto.ResidenceProgramDto;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.entity.ResidenceProgram;
import org.itmo.isLab1.residences.mapper.ResidenceProgramMapper;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.itmo.isLab1.residences.repository.ResidenceProgramRepository;
import org.itmo.isLab1.users.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResidenceProgramService {

    private final ResidenceProgramRepository residenceProgramRepository;
    private final ResidenceProgramMapper residenceProgramMapper;
    private final UserService userService;
    private final ResidenceDetailsService residenceDetailsService;
    private final ResidenceDetailsRepository residenceDetailsRepository;

    /**
     * Возвращает пагинированный список программ для резиденции текущего пользователя
     *
     * @param residenceId идентификатор резиденции
     * @param pageable    параметры пагинации
     * @return страница с программами резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public Page<ResidenceProgramDto> getProgramsByResidenceId(Pageable pageable) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId());
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        return residenceProgramRepository.findByResidenceId(residence.getId(), pageable)
            .map(residenceProgramMapper::toDto);
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

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId());
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        // Проверяем принадлежность программы к резиденции
        ResidenceProgram program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));

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

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId());
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        Long programId;
        try {
            programId = residenceProgramRepository.createProgram(
                    createDto.getResidenceId(),
                    createDto.getTitle(),
                    createDto.getDescription(),
                    createDto.getGoals(),
                    createDto.getConditions(),
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Программа не найдена после создания с id " + programId));

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
}
