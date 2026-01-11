package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    Page<Media> findByWorkIdOrderByCreatedAt(Long workId, Pageable pageable);
    Optional<Media> findByIdAndWorkId(Long mediaId, Long workId);
    boolean existsByIdAndWorkId(Long mediaId, Long workId);
    long countByWorkId(Long workId);
}