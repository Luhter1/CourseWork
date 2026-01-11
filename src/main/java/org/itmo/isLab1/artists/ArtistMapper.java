package org.itmo.isLab1.artists;

import org.itmo.isLab1.artists.dto.ArtistProfileResponse;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateRequest;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ArtistMapper {
    
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "email")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.surname", target = "surname")
    @Mapping(source = "details.biography", target = "biography")
    @Mapping(source = "details.location", target = "location")
    ArtistProfileResponse toProfileResponse(User user, ArtistDetails details);
    
    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    void updateUserFromRequest(ArtistProfileUpdateRequest request, @MappingTarget User user);
}
