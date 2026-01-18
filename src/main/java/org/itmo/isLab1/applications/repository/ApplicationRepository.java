package org.itmo.isLab1.applications.repository;

import org.itmo.isLab1.applications.entity.Application;
import org.itmo.isLab1.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByArtist(User artist, Pageable pageable);
}
