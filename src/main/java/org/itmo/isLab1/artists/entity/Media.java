package org.itmo.isLab1.artists.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.utils.datetime.ZonedDateTimeConverter;
import org.itmo.isLab1.artists.dto.MediaTypeEnum;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_media")
public class Media implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_media_id_seq")
    @SequenceGenerator(name = "art2art_media_id_seq", sequenceName = "art2art_media_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    @NotNull(message = "Work is required")
    private Work work;

    @Column(name = "uri", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "URI is required")
    private String uri;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "media_type", nullable = false)
    @NotNull(message = "Media type is required")
    @ColumnTransformer(write = "?::art2art_media_type_enum")
    private MediaTypeEnum mediaType;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;
}