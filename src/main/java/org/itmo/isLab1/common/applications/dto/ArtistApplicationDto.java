package org.itmo.isLab1.common.applications.dto;
import lombok.Data;
import java.time.Instant;

// TODO
@Data
public class ArtistApplicationDto {
    private Long id;
    private Long programId;
    private String status;
    private Instant submittedAt;
    private Instant createdAt;
}
