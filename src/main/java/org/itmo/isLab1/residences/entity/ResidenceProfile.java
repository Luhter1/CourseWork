package org.itmo.isLab1.residences.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.utils.datetime.ZonedDateTimeConverter;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_residence_details")
public class ResidenceProfile implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_residence_details_id_seq")
    @SequenceGenerator(name = "art2art_residence_details_id_seq", sequenceName = "art2art_residence_details_id_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название должно содержать максимум 255 символов")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Местоположение не может быть пустым")
    @Column(name = "location", length = 255)
    private String location;

    @NotBlank(message = "Контакты не могут быть пустыми")
    @Column(name = "contacts", columnDefinition = "JSONB")
    private String contacts;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // TODO: Добавить связь с ValidationRequest после создания сущности ValidationRequest
    // @OneToOne(mappedBy = "residence", cascade = CascadeType.ALL, orphanRemoval = true)
    // private ValidationRequest validationRequest;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
