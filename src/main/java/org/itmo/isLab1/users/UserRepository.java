package org.itmo.isLab1.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Page<User> findByRole(Role role, Pageable pageable);
  Optional<User> findById(Long id);
  boolean existsByUsername(String username);
}