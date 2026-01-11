package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    
    Page<Work> findByArtistId(Long artistId, Pageable pageable);
    Optional<Work> findByIdAndArtistId(Long id, Long artistId);
    boolean existsByIdAndArtistId(Long id, Long artistId);
}
