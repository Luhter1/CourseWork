package org.itmo.isLab1.residences.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.residences.dto.ResidenceProgramCreateDto;
import org.itmo.isLab1.residences.dto.ResidenceProgramDto;
import org.itmo.isLab1.residences.dto.ResidenceProgramUpdateDto;
import org.itmo.isLab1.residences.entity.ResidenceProgram;
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

    @Mapping(source = "residence.id", target = "residenceId")
    ResidenceProgramDto toDto(ResidenceProgram entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "furtherActionsSentAt", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    ResidenceProgram toEntity(ResidenceProgramCreateDto createDto);

    void updateEntity(ResidenceProgramUpdateDto updateDto, @MappingTarget ResidenceProgram entity);
}
