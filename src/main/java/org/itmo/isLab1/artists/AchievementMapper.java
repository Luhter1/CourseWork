package org.itmo.isLab1.artists;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "createDto.type", target = "type")
    @Mapping(source = "createDto.title", target = "title")
    @Mapping(source = "createDto.description", target = "description")
    @Mapping(source = "createDto.link", target = "link")
    @Mapping(source = "artist", target = "artist")
    Achievement toEntity(AchievementCreateDto createDto, ArtistProfile artist);
    
    void updateEntityFromDto(AchievementUpdateDto updateDto, @MappingTarget Achievement achievement);
}
