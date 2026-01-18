package org.itmo.isLab1.experts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpertDto {
    private final Long userId;
    private final String email;
    private final String name;
    private final String surname;
}
