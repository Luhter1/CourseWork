package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.dto.WorkRequest;
import org.itmo.isLab1.artists.dto.WorkResponse;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.entity.Work;
import org.itmo.isLab1.artists.repository.ArtistDetailsRepository;
import org.itmo.isLab1.artists.repository.WorkRepository;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления работами художников.
 * Реализует бизнес-логику CRUD операций над портфолио работ художников.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistWorkService {

    private final WorkRepository workRepository;
    private final ArtistDetailsRepository artistDetailsRepository;
    private final UserRepository userRepository;
    // TODO: Добавить WorkMapper после его создания
    // private final WorkMapper workMapper;

    /**
     * Получает все работы авторизованного художника с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница работ в виде DTO
     */
    public Page<WorkResponse> getWorksForArtist(Pageable pageable, Long artistId) {        
        return workRepository.findByArtistId(artistId, pageable)
                .map(this::mapToWorkResponse);
    }
    
    /**
     * Получает все работы текущего авторизованного художника с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница работ в виде DTO
     */
    public Page<WorkResponse> getWorksForCurrentArtist(Pageable pageable) {
        Long artistId = getCurrentArtistId();
        
        // Получаем работы художника с пагинацией и конвертируем в DTO
        return workRepository.findByArtistId(artistId, pageable)
                .map(this::mapToWorkResponse);
    }

    /**
     * Создает новую работу для авторизованного художника.
     *
     * @param request данные для создания работы
     * @return созданная работа в виде DTO
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    @Transactional
    public WorkResponse createWork(WorkRequest request) {
        Long artistId = getCurrentArtistId();
        
        // Находим художника
        ArtistDetails artist = artistDetailsRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));
        
        // Создаем сущность Work
        Work work = Work.builder()
                .artist(artist)
                .title(request.getTitle())
                .description(request.getDescription())
                .artDirection(request.getArtDirection())
                .date(request.getDate())
                .link(request.getLink())
                .build();
        
        // Сохраняем и возвращаем DTO
        Work savedWork = workRepository.save(work);
        return mapToWorkResponse(savedWork);
    }

    /**
     * Обновляет существующую работу художника.
     *
     * @param id      ID работы
     * @param request данные для обновления
     * @return обновленная работа в виде DTO
     * @throws ResourceNotFoundException если работа не найдена или не принадлежит художнику
     */
    @Transactional
    public WorkResponse updateWork(Long id, WorkRequest request) {
        Long artistId = getCurrentArtistId();
        
        // Находим работу и проверяем принадлежность художнику
        Work work = workRepository.findByIdAndArtistId(id, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Работа с ID " + id + " не найдена для текущего художника"));
        
        // Обновляем поля
        work.setTitle(request.getTitle());
        work.setDescription(request.getDescription());
        work.setArtDirection(request.getArtDirection());
        work.setDate(request.getDate());
        work.setLink(request.getLink());
        
        // Сохраняем и возвращаем DTO
        Work updatedWork = workRepository.save(work);
        return mapToWorkResponse(updatedWork);
    }

    /**
     * Удаляет работу художника.
     *
     * @param id ID работы
     * @throws ResourceNotFoundException если работа не найдена или не принадлежит художнику
     */
    @Transactional
    public void deleteWork(Long id) {
        Long artistId = getCurrentArtistId();
        
        // Проверяем существование работы и принадлежность художнику
        if (!workRepository.existsByIdAndArtistId(id, artistId)) {
            throw new ResourceNotFoundException(
                    "Работа с ID " + id + " не найдена для текущего художника");
        }
        
        // Удаляем работу
        workRepository.deleteById(id);
    }

    /**
     * Вспомогательный метод для получения ID текущего художника из контекста безопасности.
     *
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    private Long getCurrentArtistId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с username " + username + " не найден"));
        
        ArtistDetails artistDetails = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));
        
        return artistDetails.getId();
    }

    /**
     * Вспомогательный метод для маппинга Work в WorkResponse.
     * TODO: Заменить на использование WorkMapper после его создания.
     *
     * @param work сущность работы
     * @return DTO работы
     */
    private WorkResponse mapToWorkResponse(Work work) {
        return WorkResponse.builder()
                .id(work.getId())
                .title(work.getTitle())
                .description(work.getDescription())
                .artDirection(work.getArtDirection())
                .date(work.getDate())
                .link(work.getLink())
                .createdAt(work.getCreatedAt())
                .updatedAt(work.getUpdatedAt())
                .build();
    }
}
