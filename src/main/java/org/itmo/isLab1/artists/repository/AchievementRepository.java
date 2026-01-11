package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByArtistIdOrderByCreatedAtDesc(Long artistId);
    Optional<Achievement> findByIdAndArtistId(Long id, Long artistId);
}
