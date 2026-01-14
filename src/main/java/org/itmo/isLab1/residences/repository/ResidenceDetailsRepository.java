package org.itmo.isLab1.residences.repository;

import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.itmo.isLab1.residences.entity.ValidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidenceDetailsRepository extends JpaRepository<ResidenceDetails, Long> {

    @Query(
        value = """
            select create_residence_profile(
                :title,
                :description,
                :location,
                cast(:contacts as jsonb),
                :userId
            )
            """,
        nativeQuery = true
    )
    Long createResidenceProfile(
        @Param("title") String title,
        @Param("description") String description,
        @Param("location") String location,
        @Param("contacts") String contacts,
        @Param("userId") Long userId
    );

    Page<ResidenceDetails> findByValidationStatus(ValidationStatus status, Pageable pageable);

    Page<ResidenceDetails> findByIsPublishedTrue(Pageable pageable);

    Optional<ResidenceDetails> findByUserId(Long userId);

}