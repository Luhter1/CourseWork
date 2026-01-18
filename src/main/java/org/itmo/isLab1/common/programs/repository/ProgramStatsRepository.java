package org.itmo.isLab1.common.programs.repository;

import org.itmo.isLab1.common.programs.entity.ProgramStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ProgramStatsRepository extends JpaRepository<ProgramStats, Long> {
    @Query(
        value = """
            select log_program_view(
                :programId
            )
            """,
        nativeQuery = true
    )
    void createProgramViewLog(
        @Param("programId") Long programId
    );

    Optional<ProgramStats> findByProgramId(Long programId);
}
