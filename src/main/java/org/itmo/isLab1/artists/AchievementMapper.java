package org.itmo.isLab1.artists;

import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementResponseDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    
    AchievementResponseDto toResponseDto(Achievement achievement);
    
    Achievement toEntity(AchievementCreateDto createDto);
    
    void updateEntityFromDto(AchievementUpdateDto updateDto, @MappingTarget Achievement achievement);
}
