package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Page<Achievement> findByArtistIdOrderByCreatedAtDesc(Long artistId, Pageable pageable);
    Optional<Achievement> findByIdAndArtistId(Long id, Long artistId);
}
