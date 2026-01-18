package org.itmo.isLab1.reviews.repository;

import org.itmo.isLab1.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByProgramIdAndArtistId(Long programId, Long artistId);

    Page<Review> findByProgramId(Long programId, Pageable pageable);
}
