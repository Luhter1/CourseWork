package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistDetailsRepository extends JpaRepository<ArtistDetails, Long> {
    Optional<ArtistDetails> findByUser(User user);
    Optional<ArtistDetails> findById(Long id);
}