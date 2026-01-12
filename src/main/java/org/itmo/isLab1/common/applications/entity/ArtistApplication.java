package org.itmo.isLab1.common.applications.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.utils.datetime.ZonedDateTimeConverter;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_application_requests")
public class ArtistApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_application_requests_id_seq")
    @SequenceGenerator(name = "art2art_application_requests_id_seq", sequenceName = "art2art_application_requests_id_seq", allocationSize = 1)
    private Long id;

    // Связь с программой
    // TODO: можно заменить на ManyToOne, если есть сущность Program
    @Column(name = "program_id", nullable = false)
    private Long programId; // можно заменить на ManyToOne, если есть сущность Program

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User artist;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", nullable = false)
    @NotNull(message = "Application request status is required")
    @ColumnTransformer(write="?::art2art_application_request_status")
    private ApplicationRequestEnum status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
