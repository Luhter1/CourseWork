package org.itmo.isLab1.residences.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.residences.dto.ResidenceStatsDto;
import org.itmo.isLab1.residences.entity.ResidenceStats;
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
public interface ResidenceStatsMapper {

    @Mapping(source = "viewsCount", target = "viewsCount")
    ResidenceStatsDto toDto(ResidenceStats stats);

}
