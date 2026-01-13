package org.itmo.isLab1.residences.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.residences.dto.ResidenceDetailsUpdateDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsCreateDto;
import org.itmo.isLab1.residences.dto.ResidenceDetailsDto;
import org.itmo.isLab1.residences.dto.ValidationResponseDto;
import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.users.User;
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
public interface ResidenceDetailsMapper {
    
    @Mapping(source = "details.user.id", target = "userId")
    ValidationResponseDto toValidationResponse(ResidenceDetails details);

    ResidenceDetailsDto toResidenceDetails(ResidenceDetails details);
    
    void updateResidenceDetails(ResidenceDetailsUpdateDto request, @MappingTarget ResidenceDetails details);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "user")
    ResidenceDetails toResidenceDetails(ResidenceDetailsCreateDto request, User user);
    
}
