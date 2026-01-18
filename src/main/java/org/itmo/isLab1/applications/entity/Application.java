package org.itmo.isLab1.applications.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.programs.entity.Program;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_application_requests")
public class Application implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_application_requests_id_seq")
    @SequenceGenerator(name = "art2art_application_requests_id_seq", sequenceName = "art2art_application_requests_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    @NotNull(message = "Program is required")
    private Program program;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Application request status is required")
    @ColumnTransformer(write="?::art2art_application_request_status")
    private ApplicationRequestEnum status;
    
    @Column(name = "submitted_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime submittedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

}
