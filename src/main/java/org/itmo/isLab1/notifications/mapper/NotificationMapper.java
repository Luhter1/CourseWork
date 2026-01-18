package org.itmo.isLab1.notifications.mapper;

import org.itmo.isLab1.common.mapper.JsonNullableMapper;
import org.itmo.isLab1.common.mapper.ReferenceMapper;
import org.itmo.isLab1.notifications.dto.NotificationCreateDto;
import org.itmo.isLab1.notifications.dto.NotificationDto;
import org.itmo.isLab1.notifications.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    uses = { JsonNullableMapper.class, ReferenceMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    Notification toEntity(NotificationCreateDto dto);

    NotificationDto toDto(Notification entity);
}
