package org.itmo.isLab1.artists;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.artists.dto.WorkCreateDto;
import org.itmo.isLab1.artists.dto.WorkDto;
import org.itmo.isLab1.artists.dto.WorkUpdateDto;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.entity.Work;
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
public interface WorkMapper {
    WorkDto toResponseDto(Work work);
    
    @Mapping(source = "artist", target = "artist")
    @Mapping(source = "createDto.title", target = "title")
    @Mapping(source = "createDto.description", target = "description")
    @Mapping(source = "createDto.artDirection", target = "artDirection")
    @Mapping(source = "createDto.date", target = "date")
    @Mapping(source = "createDto.link", target = "link")
    Work toEntity(WorkCreateDto createDto, ArtistDetails artist);

    @Mapping(source = "updateDto.title", target = "title")
    @Mapping(source = "updateDto.description", target = "description")
    @Mapping(source = "updateDto.artDirection", target = "artDirection")
    @Mapping(source = "updateDto.date", target = "date")
    @Mapping(source = "updateDto.link", target = "link")
    void updateEntityFromDto(WorkUpdateDto updateDto, @MappingTarget Work work);
}
