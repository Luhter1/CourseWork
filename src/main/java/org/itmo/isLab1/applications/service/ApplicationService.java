package org.itmo.isLab1.applications.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.applications.dto.ApplicationCreateDto;
import org.itmo.isLab1.applications.dto.ApplicationDto;
import org.itmo.isLab1.applications.entity.Application;
import org.itmo.isLab1.applications.entity.ApplicationRequestEnum;
import org.itmo.isLab1.applications.mapper.ApplicationMapper;
import org.itmo.isLab1.applications.repository.ApplicationRepository;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
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

    public ApplicationDto confirmMyApplication(Long applicationId) {
        User currentUser = userService.getCurrentUser();

        Application application =  applicationRepository.findByArtistAndId(currentUser, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));

        if(application.getStatus() != ApplicationRequestEnum.APPROVED){
            throw new PolicyViolationError("Нельзя подтвердить неодобренную заявку");
        }

        application.setStatus(ApplicationRequestEnum.CONFIRMED);

        Application newApplication = applicationRepository.save(application);

        return applicationMapper.toApplicationDto(newApplication);
    }

    public ApplicationDto declineMyApplication(Long applicationId) {
        User currentUser = userService.getCurrentUser();

        Application application =  applicationRepository.findByArtistAndId(currentUser, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));

        if(application.getStatus() != ApplicationRequestEnum.APPROVED){
            throw new PolicyViolationError("Нельзя отклонить неодобренную заявку");
        }

        application.setStatus(ApplicationRequestEnum.DECLINED_BY_ARTIST);

        Application newApplication = applicationRepository.save(application);

        return applicationMapper.toApplicationDto(newApplication);
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
