package org.itmo.isLab1.common.programs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;
import org.itmo.isLab1.residences.entity.ResidenceDetails;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_programs")
public class Program implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_programs_id_seq")
    @SequenceGenerator(name = "art2art_programs_id_seq", sequenceName = "art2art_programs_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residence_id", nullable = false)
    @NotNull(message = "Residence is required")
    private ResidenceDetails residence;

    @Column(name = "title", nullable = false, length = 255)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "goals", columnDefinition = "jsonb")
    private Map<String, Object> goals;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions", columnDefinition = "jsonb")
    private Map<String, Object> conditions;

    @Column(name = "deadline_apply", nullable = false)
    @NotNull(message = "Deadline apply is required")
    private LocalDate deadlineApply;

    @Column(name = "deadline_review", nullable = false)
    @NotNull(message = "Deadline review is required")
    private LocalDate deadlineReview;

    @Column(name = "deadline_notify", nullable = false)
    @NotNull(message = "Deadline notify is required")
    private LocalDate deadlineNotify;

    @Column(name = "duration_days")
    @Min(value = 0, message = "Duration days must be non-negative")
    @Max(value = 360, message = "Duration days must less than year")
    private Integer durationDays;

    @Column(name = "budget_quota")
    @Min(value = 0, message = "Budget quota must be non-negative")
    @Max(value = 10000000, message = "Budget quota must be less than 10 million")
    private Integer budgetQuota;

    @Column(name = "people_quota")
    @Min(value = 0, message = "People quota must be non-negative")
    @Max(value = 1000, message = "People quota must be less than 1000")
    private Integer peopleQuota;

    @Column(name = "futher_actions_sent_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime furtherActionsSentAt;

    @Column(name = "is_published", nullable = false)
    @NotNull(message = "Is published is required")
    private Boolean isPublished;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
