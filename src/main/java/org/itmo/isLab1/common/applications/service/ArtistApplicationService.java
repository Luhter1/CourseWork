package org.itmo.isLab1.common.applications.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.artists.service.ArtistService;
import org.itmo.isLab1.common.applications.dto.ArtistApplicationDto;
import org.itmo.isLab1.common.applications.entity.ArtistApplication;
import org.itmo.isLab1.common.applications.repository.ArtistApplicationRequestRepository;
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
public class ArtistApplicationService {

    private final ArtistApplicationRequestRepository applicationRepository;
    private final UserRepository userRepository;

    public Page<ArtistApplicationDto> getMyApplications(Pageable pageable) {
        User currentUser = getCurrentUser();

        return applicationRepository.findAllByArtist(currentUser, pageable)
                .map(this::toDto);
    }

    //TODO: маппер
    private ArtistApplicationDto toDto(ArtistApplication request) {
        ArtistApplicationDto dto = new ArtistApplicationDto();
        dto.setId(request.getId());
        dto.setProgramId(request.getProgramId());
        dto.setStatus(request.getStatus().name()); // enum -> string
        dto.setSubmittedAt(request.getSubmittedAt());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
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
