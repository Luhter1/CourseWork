package org.itmo.isLab1.programs.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.programs.dto.ProgramDto;
import org.itmo.isLab1.programs.dto.ProgramPreviewDto;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.programs.mapper.ProgramMapper;
import org.itmo.isLab1.programs.repository.ProgramRepository;
import org.itmo.isLab1.programs.repository.ProgramStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramService {
    
    private final ProgramRepository residenceProgramRepository;
    private final ProgramStatsRepository residenceProgramStatsRepository;
    private final ProgramMapper residenceProgramMapper;

    /**
     * Возвращает список программ
     *
     * @param pageable    параметры пагинации
     * @return страница с программами резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public Page<ProgramPreviewDto> getPrograms(Pageable pageable) {
        return residenceProgramRepository.findByIsPublishedTrueAndDeadlineApplyGreaterThanEqual(LocalDate.now(), pageable)
            .map(residenceProgramMapper::toPreviewDto);
    }

    /**
     * Возвращает программу по идентификатору
     *
     * @param residenceId идентификатор резиденции
     * @param id          идентификатор программы
     * @return DTO программы
     * @throws ResourceNotFoundException если программа или резиденция не найдены
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional
    public ProgramDto getProgramById(Long programId) {

        Program program = residenceProgramRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));

        if (!program.getIsPublished()) {
            throw new PolicyViolationError("Доступ к программе возможен только если она опубликована");
        }

        residenceProgramStatsRepository.createProgramViewLog(programId);
        return residenceProgramMapper.toDto(program);
    }
}
