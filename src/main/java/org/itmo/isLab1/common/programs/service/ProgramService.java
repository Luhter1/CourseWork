package org.itmo.isLab1.common.programs.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramPreviewDto;
import org.itmo.isLab1.common.programs.entity.ResidenceProgram;
import org.itmo.isLab1.common.programs.mapper.ResidenceProgramMapper;
import org.itmo.isLab1.common.programs.repository.ResidenceProgramRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramService {
    
    private final ResidenceProgramRepository residenceProgramRepository;
    private final ResidenceProgramMapper residenceProgramMapper;

    /**
     * Возвращает список программ
     *
     * @param pageable    параметры пагинации
     * @return страница с программами резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     * @throws PolicyViolationError       если пользователь не является владельцем резиденции
     */
    @Transactional(readOnly = true)
    public Page<ResidenceProgramPreviewDto> getPrograms(Pageable pageable) {
        return residenceProgramRepository.findAll(pageable)
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
    @Transactional(readOnly = true)
    public ResidenceProgramDto getProgramById(Long programId) {

        ResidenceProgram program = residenceProgramRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Программа с id " + programId + " не найдена для резиденции"));
        return residenceProgramMapper.toDto(program);
    }
}
