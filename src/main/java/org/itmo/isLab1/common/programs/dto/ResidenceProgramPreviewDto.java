package org.itmo.isLab1.common.programs.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceProgramPreviewDto {
    private Long id;
    private Long residenceId;
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadlineApply;
}
