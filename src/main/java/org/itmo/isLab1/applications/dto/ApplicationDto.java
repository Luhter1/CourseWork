package org.itmo.isLab1.applications.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.itmo.isLab1.applications.entity.ApplicationRequestEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;
    private Long programId;
    private Long userId;
    private ApplicationRequestEnum status;
}
