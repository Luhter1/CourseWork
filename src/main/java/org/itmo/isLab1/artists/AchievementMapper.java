package org.itmo.isLab1.artists;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AchievementMapper {
    
    AchievementDto toResponseDto(Achievement achievement);
    
    Achievement toEntity(AchievementCreateDto createDto);
    
    void updateEntityFromDto(AchievementUpdateDto updateDto, @MappingTarget Achievement achievement);
}
