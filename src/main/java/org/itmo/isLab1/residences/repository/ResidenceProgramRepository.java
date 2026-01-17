package org.itmo.isLab1.residences.repository;

import org.itmo.isLab1.residences.entity.ResidenceProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ResidenceProgramRepository extends JpaRepository<ResidenceProgram, Long> {

    Page<ResidenceProgram> findByResidenceId(Long residenceId, Pageable pageable);

    Optional<ResidenceProgram> findByResidenceIdAndId(Long residenceId, Long id);

    @Query(
        value = """
            select create_program(
                :residenceId, 
                :title, 
                :description, 
                :goals::jsonb, 
                :conditions::jsonb,
                :deadlineApply, 
                :deadlineReview, 
                :deadlineNotify,
                :durationDays, 
                :budgetQuota, 
                :peopleQuota, 
                :creatorUserId
            )
            """, 
        nativeQuery = true
    )
    Long createProgram(
        @Param("residenceId") Long residenceId,
        @Param("title") String title,
        @Param("description") String description,
        @Param("goals") Map<String, Object> goals,
        @Param("conditions") Map<String, Object> conditions,
        @Param("deadlineApply") LocalDate deadlineApply,
        @Param("deadlineReview") LocalDate deadlineReview,
        @Param("deadlineNotify") LocalDate deadlineNotify,
        @Param("durationDays") Integer durationDays,
        @Param("budgetQuota") Integer budgetQuota,
        @Param("peopleQuota") Integer peopleQuota,
        @Param("creatorUserId") Long creatorUserId
    );
}
