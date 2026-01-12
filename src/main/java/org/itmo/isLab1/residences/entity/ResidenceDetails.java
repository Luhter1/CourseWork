package org.itmo.isLab1.residences.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.utils.datetime.ZonedDateTimeConverter;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_residence_details")
public class ResidenceDetails implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_residence_details_id_seq")
    @SequenceGenerator(name = "art2art_residence_details_id_seq", sequenceName = "art2art_residence_details_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contacts", columnDefinition = "jsonb")
    private Map<String, Object> contacts;

    @Column(name = "location", length = 255)
    private String location;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "validation_status", nullable = false)
    @NotNull(message = "Validation status is required")
    @ColumnTransformer(write="?::art2art_validation_requests_status")
    private ValidationStatus validationStatus;

    @Column(name = "validation_comment", columnDefinition = "TEXT")
    private String validationComment;

    @Column(name = "validation_submitted_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime validationSubmittedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
