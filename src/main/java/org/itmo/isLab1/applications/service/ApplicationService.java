package org.itmo.isLab1.applications.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.applications.dto.ApplicationCreateDto;
import org.itmo.isLab1.applications.dto.ApplicationDto;
import org.itmo.isLab1.applications.mapper.ApplicationMapper;
import org.itmo.isLab1.applications.repository.ApplicationRepository;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final ApplicationMapper applicationMapper;

    public Page<ApplicationDto> getMyApplications(Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        return applicationRepository.findAllByArtist(currentUser, pageable)
                .map(applicationMapper::toApplicationDto);
    }

    public Long createApplication(Long programId, ApplicationCreateDto applicationDto) {
        User currentUser = userService.getCurrentUser();

        return applicationRepository.createApplication(
            currentUser.getId(), 
            programId,
            applicationDto.getMotivation()
        );
    }


}
