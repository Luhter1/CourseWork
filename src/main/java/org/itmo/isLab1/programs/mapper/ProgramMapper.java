package org.itmo.isLab1.programs.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.programs.dto.ProgramCreateDto;
import org.itmo.isLab1.programs.dto.ProgramDto;
import org.itmo.isLab1.programs.dto.ProgramPreviewDto;
import org.itmo.isLab1.programs.dto.ProgramStatsDto;
import org.itmo.isLab1.programs.dto.ProgramUpdateDto;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.programs.entity.ProgramStats;
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
public interface ProgramMapper {

    ProgramStatsDto toStatDto(ProgramStats entity);

    @Mapping(target = "previewDto", expression = "java(toPreviewDto(entity))")
    ProgramDto toDto(Program entity);

    @Mapping(source = "residence.id", target = "residenceId")
    ProgramPreviewDto toPreviewDto(Program entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "furtherActionsSentAt", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    Program toEntity(ProgramCreateDto createDto);

    void updateEntity(ProgramUpdateDto updateDto, @MappingTarget Program entity);
}
