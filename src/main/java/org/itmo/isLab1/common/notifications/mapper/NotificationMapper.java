package org.itmo.isLab1.common.notifications.mapper;

import org.itmo.isLab1.common.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.common.notifications.dto.NotificationDto;
import org.itmo.isLab1.common.notifications.entity.Notification;
import org.itmo.isLab1.common.notifications.entity.NotificationCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    @Mapping(source = "category", target = "category", qualifiedByName = "stringToCategory")
    Notification toEntity(NotificationCreateDto dto);

    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToString")
    NotificationDto toDto(Notification entity);

    @Named("stringToCategory")
    default NotificationCategory stringToCategory(String category) {
        if (category == null) {
            return null;
        }
        return NotificationCategory.valueOf(category.toUpperCase());
    }

    @Named("categoryToString")
    default String categoryToString(NotificationCategory category) {
        if (category == null) {
            return null;
        }
        return category.name().toLowerCase();
    }
}
