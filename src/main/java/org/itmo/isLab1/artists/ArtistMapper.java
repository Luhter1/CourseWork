package org.itmo.isLab1.artists;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
import org.itmo.isLab1.artists.entity.ArtistProfile;
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
public interface ArtistMapper {
    
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "email")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.surname", target = "surname")
    @Mapping(source = "details.biography", target = "biography")
    @Mapping(source = "details.location", target = "location")
    ArtistProfileDto toProfileResponse(User user, ArtistProfile details);
    
    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    void updateUserFromRequest(ArtistProfileUpdateDto request, @MappingTarget User user);

    @Mapping(source = "biography", target = "biography")
    @Mapping(source = "location", target = "location")
    void updateArtistDetailsFromRequest(ArtistProfileUpdateDto request, @MappingTarget ArtistProfile details);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "request.biography", target = "biography")
    @Mapping(source = "request.location", target = "location")
    ArtistProfile toArtistDetails(ArtistProfileCreateDto request, User user);
    
}
