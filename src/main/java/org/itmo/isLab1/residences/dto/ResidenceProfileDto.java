package org.itmo.isLab1.residences.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResidenceProfileDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String location;
    private final String contacts;
    private final Boolean isPublished;
}