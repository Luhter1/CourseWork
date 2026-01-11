package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.WorkMapper;
import org.itmo.isLab1.artists.dto.WorkCreateDto;
import org.itmo.isLab1.artists.dto.WorkUpdateDto;
import org.itmo.isLab1.artists.dto.WorkDto;
import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.itmo.isLab1.artists.entity.Work;
import org.itmo.isLab1.artists.repository.ArtistProfileRepository;
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


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistWorkService {

    private final WorkRepository workRepository;
    private final ArtistProfileRepository artistDetailsRepository;
    private final UserRepository userRepository;
    private final WorkMapper workMapper;

    /**
     * Получает все работы авторизованного художника с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница работ в виде DTO
     */
    public Page<WorkDto> getWorksForArtist(Pageable pageable, Long userId) {    
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с id " + userId + " не найден"));
        
        ArtistProfile artistDetails = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));

        return workRepository.findByArtistId(artistDetails.getId(), pageable)
                .map(workMapper::toResponseDto);
    }
    
    /**
     * Получает все работы текущего авторизованного художника с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница работ в виде DTO
     */
    public Page<WorkDto> getWorksForCurrentArtist(Pageable pageable) {
        Long artistId = getCurrentArtistId();
        
        // Получаем работы художника с пагинацией и конвертируем в DTO
        return workRepository.findByArtistId(artistId, pageable)
                .map(workMapper::toResponseDto);
    }

    /**
     * Создает новую работу для авторизованного художника.
     *
     * @param request данные для создания работы
     * @return созданная работа в виде DTO
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    @Transactional
    public WorkDto createWork(WorkCreateDto request) {
        Long artistId = getCurrentArtistId();
        
        // Находим художника
        ArtistProfile artist = artistDetailsRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));

        Work work = workMapper.toEntity(request, artist);
        Work savedWork = workRepository.save(work);

        return workMapper.toResponseDto(savedWork);
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
    public WorkDto updateWork(Long id, WorkUpdateDto request) {
        Long artistId = getCurrentArtistId();
        
        // Находим работу и проверяем принадлежность художнику
        Work work = workRepository.findByIdAndArtistId(id, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Работа с ID " + id + " не найдена для текущего художника"));
        
        workMapper.updateEntityFromDto(request, work);
        
        // Сохраняем и возвращаем DTO
        Work updatedWork = workRepository.save(work);
        return workMapper.toResponseDto(updatedWork);
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
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с username " + username + " не найден"));
        
        ArtistProfile artistDetails = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));
        
        return artistDetails.getId();
    }
}
