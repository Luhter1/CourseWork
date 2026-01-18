package org.itmo.isLab1.programs.repository;

import org.itmo.isLab1.programs.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    Page<Program> findByResidenceId(Long residenceId, Pageable pageable);

    Optional<Program> findById(Long id);

    Optional<Program> findByResidenceIdAndId(Long residenceId, Long id);

    Page<Program> findByIsPublishedTrue(Pageable pageable);

    @Query(
        value = """
            select create_program(
                :residenceId, 
                :title, 
                :description, 
                cast(:goals as jsonb), 
                cast(:conditions as jsonb),
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
        @Param("goals") String goals,
        @Param("conditions") String conditions,
        @Param("deadlineApply") LocalDate deadlineApply,
        @Param("deadlineReview") LocalDate deadlineReview,
        @Param("deadlineNotify") LocalDate deadlineNotify,
        @Param("durationDays") Integer durationDays,
        @Param("budgetQuota") Integer budgetQuota,
        @Param("peopleQuota") Integer peopleQuota,
        @Param("creatorUserId") Long creatorUserId
    );
}
