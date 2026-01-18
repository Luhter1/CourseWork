package org.itmo.isLab1.experts.repository;

import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertRepository extends JpaRepository<ArtistProfile, Long> {
    @Query(
        value = """
            select assign_expert_to_program(
                :programId, 
                :expertId, 
                :assigner
            )
            """, 
        nativeQuery = true
    )
    Long assignExpertToProgram(
        @Param("programId") Long programId,
        @Param("expertId") Long expertId,
        @Param("assigner") Long assigner
    );
}
