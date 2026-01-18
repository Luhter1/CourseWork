package org.itmo.isLab1.applications.repository;

import java.util.Collection;
import java.util.Optional;

import org.itmo.isLab1.applications.entity.Application;
import org.itmo.isLab1.applications.entity.ApplicationRequestEnum;
import org.itmo.isLab1.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByArtistAndStatusIn(
            User artist,
            Collection<ApplicationRequestEnum> statuses,
            Pageable pageable
    );

    Page<Application> findAllByArtist(User artist, Pageable pageable);

    Optional<Application> findByArtistAndId(User artist, Long applicationId);

    @Query(
        value = """
            select submit_application(
                :userId, 
                :programId, 
                :motivation
            )
            """, 
        nativeQuery = true
    )
    Long createApplication(
        @Param("userId") Long userId,
        @Param("programId") Long programId,
        @Param("motivation") String motivation
    );
}
