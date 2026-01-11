package org.itmo.isLab1.artists;

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
public interface ArtistDetailsMapper {
    
    @Mapping(source = "user", target = "user")
    @Mapping(source = "request.biography", target = "biography")
    @Mapping(source = "request.location", target = "location")
    ArtistDetails toArtistDetails(ArtistProfileUpdateRequest request, User user);
    
    @Mapping(source = "biography", target = "biography")
    @Mapping(source = "location", target = "location")
    void updateArtistDetailsFromRequest(ArtistProfileUpdateRequest request, @MappingTarget ArtistDetails details);
}
