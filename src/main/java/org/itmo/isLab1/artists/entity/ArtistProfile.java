package org.itmo.isLab1.artists.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;
import org.itmo.isLab1.users.User;

import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_artist_details")
public class ArtistProfile implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_artist_details_id_seq")
    @SequenceGenerator(name = "art2art_artist_details_id_seq", sequenceName = "art2art_artist_details_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "location", length = 255)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;
}
