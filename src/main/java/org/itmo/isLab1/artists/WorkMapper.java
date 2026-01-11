package org.itmo.isLab1.artists;

import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementResponseDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WorkMapper {
    WorkResponse toResponseDto(Work work);
    
    Work toEntity(WorkRequest createDto);
    
    void updateEntityFromDto(WorkRequest updateDto, @MappingTarget Work work);
}
