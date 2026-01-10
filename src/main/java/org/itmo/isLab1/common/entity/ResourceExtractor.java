package org.itmo.isLab1.common.entity;

import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ResourceExtractor {
  public Pair<String, Integer> getIdentification(BaseEntity entity) {
    return new ImmutablePair<>(
      entity.getClass().getAnnotation(Table.class).name().toLowerCase().replace('_', '-'),
      entity.getId()
    );
  }
}
