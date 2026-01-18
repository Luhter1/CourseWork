package org.itmo.isLab1.common.programs.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_program_stats")
public class ProgramStats {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_program_stats_id_seq")
    @SequenceGenerator(name = "art2art_program_stats_id_seq", sequenceName = "art2art_program_stats_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, unique = true)
    private Program program;

    @Column(name = "views_count", nullable = false)
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "applications_count", nullable = false)
    @Builder.Default
    private Integer applicationsCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
