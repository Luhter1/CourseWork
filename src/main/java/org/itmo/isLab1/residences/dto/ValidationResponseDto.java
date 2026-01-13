package org.itmo.isLab1.residences.dto;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.itmo.isLab1.residences.entity.ValidationStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponseDto {
    private ValidationStatus validationStatus;
    private String validationComment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime validationSubmittedAt;
}
