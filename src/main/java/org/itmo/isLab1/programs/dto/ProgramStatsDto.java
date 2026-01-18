package org.itmo.isLab1.programs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramStatsDto {
    private Integer viewsCount;
    private Integer applicationsCount;
}
