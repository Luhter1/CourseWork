package org.itmo.isLab1.common.programs.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramPreviewDto;
import org.itmo.isLab1.common.programs.dto.ResidenceProgramUpdateDto;
import org.itmo.isLab1.common.programs.entity.ResidenceProgram;
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
public interface ResidenceProgramMapper {

    @Mapping(target = "previewDto", expression = "java(toPreviewDto(entity))")
    ResidenceProgramDto toDto(ResidenceProgram entity);

    @Mapping(source = "residence.id", target = "residenceId")
    ResidenceProgramPreviewDto toPreviewDto(ResidenceProgram entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "furtherActionsSentAt", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    ResidenceProgram toEntity(ResidenceProgramCreateDto createDto);

    void updateEntity(ResidenceProgramUpdateDto updateDto, @MappingTarget ResidenceProgram entity);
}
