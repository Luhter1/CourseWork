package org.itmo.isLab1.residences.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.residences.dto.ResidenceStatsDto;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.entity.ResidenceStats;
import org.itmo.isLab1.residences.repository.ResidenceDetailsRepository;
import org.itmo.isLab1.residences.repository.ResidenceStatsRepository;
import org.itmo.isLab1.residences.mapper.ResidenceStatsMapper;
import org.itmo.isLab1.users.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResidenceStatsService {

    private final ResidenceStatsRepository residenceStatsRepository;
    private final ResidenceDetailsRepository residenceDetailsRepository;
    private final UserService userService;
    private final ResidenceStatsMapper residenceStatsMapper;

    /**
     * Получение статистики резиденции для текущего пользователя
     *
     * @return DTO со статистикой резиденции
     * @throws ResourceNotFoundException если резиденция не найдена
     */
    @Transactional(readOnly = true)
    public ResidenceStatsDto getStatsForCurrentUser() {
        Long userId = userService.getCurrentUser().getId();
        
        ResidenceDetails residence = residenceDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль резиденции для текущего пользователя не найден"));
        
        ResidenceStats stats = residenceStatsRepository.findByResidenceId(residence.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Статистика для резиденции с id " + residence.getId() + " не найдена"));
        
        return residenceStatsMapper.toDto(stats);
    }

}
