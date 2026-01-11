package org.itmo.isLab1.artists.dto;

import lombok.Builder;
import lombok.Data;
import org.itmo.isLab1.artists.entity.ArtDirectionEnum;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Builder
public class WorkResponse {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private ArtDirectionEnum artDirection;
    
    private LocalDate date;
    
    private String link;
}
