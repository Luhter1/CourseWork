package org.itmo.isLab1.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.itmo.isLab1.common.entity.BaseEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(Integer id, @TargetType Class<T> entityClass) {
        if (id == null) {
            return null;
        }

        T entity = entityManager.find(entityClass, id);

        if (entity == null) {
            throw new EntityNotFoundException(
                String.format("No %s found with id %d", entityClass.getSimpleName(), id)
            );
        }

        return entity;
    }

}
