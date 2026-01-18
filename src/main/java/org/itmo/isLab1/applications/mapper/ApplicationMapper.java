package org.itmo.isLab1.applications.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.applications.dto.ApplicationDto;
import org.itmo.isLab1.applications.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApplicationMapper {
    
    @Mapping(source = "artist.id", target = "userId")
    @Mapping(source = "program.id", target = "programId")
    ApplicationDto toApplicationDto(Application request);
    
}
