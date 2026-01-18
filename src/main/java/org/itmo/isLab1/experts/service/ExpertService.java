package org.itmo.isLab1.experts.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.experts.dto.ExpertDto;
import org.itmo.isLab1.users.Role;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpertService {
    
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ExpertDto> getExpertsProfile(Pageable pageable) {
        Page<User> profiles = userRepository.findByRole(Role.ROLE_EXPERT, pageable);

        return profiles.map(profile -> {
            return new ExpertDto(
                profile.getId(),
                profile.getUsername(),
                profile.getName(),
                profile.getSurname()
            );
        });
    }
}
