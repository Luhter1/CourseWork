package org.itmo.isLab1.common.applications.repository;

import org.itmo.isLab1.common.applications.entity.ArtistApplication;
import org.itmo.isLab1.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistApplicationRequestRepository extends JpaRepository<ArtistApplication, Long> {

    Page<ArtistApplication> findAllByArtist(User artist, Pageable pageable);
}
