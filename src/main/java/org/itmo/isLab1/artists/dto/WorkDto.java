package org.itmo.isLab1.artists.dto;

import lombok.Builder;
import lombok.Data;
import org.itmo.isLab1.artists.entity.ArtDirectionEnum;

import java.time.LocalDate;

@Data
@Builder
public class WorkDto {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private ArtDirectionEnum artDirection;
    
    private LocalDate date;
    
    private String link;
}
