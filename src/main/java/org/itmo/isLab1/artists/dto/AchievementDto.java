package org.itmo.isLab1.artists.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itmo.isLab1.artists.entity.AchievementType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private Long id;
    private AchievementType type;
    private String title;
    private String description;
    private String link;
}
