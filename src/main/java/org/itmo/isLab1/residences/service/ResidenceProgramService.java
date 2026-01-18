package org.itmo.isLab1.residences.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.programs.dto.ProgramCreateDto;
import org.itmo.isLab1.programs.dto.ProgramDto;
import org.itmo.isLab1.programs.dto.ProgramPreviewDto;
import org.itmo.isLab1.programs.dto.ProgramStatsDto;
import org.itmo.isLab1.programs.dto.ProgramUpdateDto;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.programs.entity.ProgramStats;
import org.itmo.isLab1.programs.mapper.ProgramMapper;
import org.itmo.isLab1.programs.repository.ProgramRepository;
import org.itmo.isLab1.programs.repository.ProgramStatsRepository;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ResidenceProgramService {

    private final ObjectMapper objectMapper;
    private final ProgramRepository residenceProgramRepository;
    private final ProgramMapper residenceProgramMapper;
    private final UserService userService;
    private final ResidenceDetailsRepository residenceDetailsRepository;
    private final ProgramStatsRepository residenceProgramStatsRepository;

    /**
     * Возвращает пагинированный список программ для резиденции текущего пользователя
     *
     * @param pageable    параметры пагинации
     * @return страница с программами резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public Page<ProgramPreviewDto> getProgramsByResidenceId(Pageable pageable) {

        User currentUser = userService.getCurrentUser();
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
    public ProgramDto getProgramById(Long programId) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        // Проверяем принадлежность программы к резиденции
        Program program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));

        return residenceProgramMapper.toDto(program);
    }


    @Transactional
    public ProgramDto publishProgram(Long programId) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        Program program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("У вам нет программы с id " + programId));

        program.setIsPublished(true);

        residenceProgramRepository.save(program);

        return residenceProgramMapper.toDto(program);
    }

    @Transactional
    public ProgramDto unpublishProgram(Long programId) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        Program program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
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
    public ProgramDto updateProgram(Long programId, ProgramUpdateDto updateDto) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));


        Program program = residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
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
    public ProgramDto createProgram(ProgramCreateDto createDto) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        String goals, conditions;
        try {
            goals = objectMapper.writeValueAsString(createDto.getGoals());
            conditions = objectMapper.writeValueAsString(createDto.getConditions());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Невозможно сериализовать цели или условия в JSON: " + e.getMessage(), e);
        }
        
        Long programId = residenceProgramRepository.createProgram(
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


        // Получаем созданную программу
        Program program = residenceProgramRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа не найдена после создания с id " + programId));

        return residenceProgramMapper.toDto(program);
    }

    /**
     * Возвращает статистику программы по идентификатору
     *
     * @param residenceId идентификатор резиденции
     * @param id          идентификатор программы
     * @return DTO программы
     * @throws ResourceNotFoundException если программа или резиденция не найдены
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional
    public ProgramStatsDto getProgramStatsById(Long programId) {

        User currentUser = userService.getCurrentUser();
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("У вас нет резиденции"));

        residenceProgramRepository.findByResidenceIdAndId(residence.getId(), programId)
                .orElseThrow(() -> new ResourceNotFoundException("У вам нет программы с id " + programId));

        ProgramStats programStats = residenceProgramStatsRepository.findByProgramId(programId)
            .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));
        return residenceProgramMapper.toStatDto(programStats);
    }

}
