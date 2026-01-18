package org.itmo.isLab1.applications.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.applications.dto.ApplicationDto;
import org.itmo.isLab1.applications.mapper.ApplicationMapper;
import org.itmo.isLab1.applications.repository.ApplicationRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    public Page<ApplicationDto> getMyApplications(Pageable pageable) {
        User currentUser = getCurrentUser();

        return applicationRepository.findAllByArtist(currentUser, pageable)
                .map(applicationMapper::toApplicationDto);
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из контекста безопасности
     *
     * @return ID пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        return user;
    }
}
