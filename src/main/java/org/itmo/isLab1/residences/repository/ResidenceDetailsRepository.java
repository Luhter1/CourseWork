package org.itmo.isLab1.residences.repository;

import org.itmo.isLab1.residences.entity.ResidenceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidenceDetailsRepository extends JpaRepository<ResidenceDetails, Long> {

    @Procedure(name = "create_residence_profile")
    Long createResidenceProfile(
            @Param("p_title") String title,
            @Param("p_description") String description,
            @Param("p_location") String location,
            @Param("p_contacts") String contacts,
            @Param("p_user_id") Long userId
    );

}