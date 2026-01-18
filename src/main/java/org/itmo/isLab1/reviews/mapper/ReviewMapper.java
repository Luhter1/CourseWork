package org.itmo.isLab1.reviews.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.reviews.dto.ReviewCreateDto;
import org.itmo.isLab1.reviews.dto.ReviewDto;
import org.itmo.isLab1.reviews.dto.ReviewUpdateDto;
import org.itmo.isLab1.reviews.entity.Review;
import org.itmo.isLab1.users.User;
import org.mapstruct.*;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewCreateDto createDto, User artist, Program program);

    @Mapping(source = "entity.artist.username", target = "artistName")
    ReviewDto toDto(Review entity);

    void updateEntity(@MappingTarget Review entity, ReviewUpdateDto updateDto);
}
