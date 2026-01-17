package org.itmo.isLab1.residences.repository;

import org.itmo.isLab1.residences.entity.ResidenceStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidenceStatsRepository extends JpaRepository<ResidenceStats, Long> {

    Optional<ResidenceStats> findByResidenceId(Long residenceId);
}
