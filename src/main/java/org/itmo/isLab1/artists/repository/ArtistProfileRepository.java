package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.itmo.isLab1.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
    Optional<ArtistProfile> findByUser(User user);
    Optional<ArtistProfile> findById(Long id);
}