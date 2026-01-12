package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.itmo.isLab1.artists.mapper.ArtistMapper;
import org.itmo.isLab1.artists.repository.ArtistProfileRepository;
import org.itmo.isLab1.common.errors.EntityDuplicateException;
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
public class ArtistService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistDetailsRepository;
    private final ArtistMapper artistMapper;

    /**
     * Получение профилей художников
     *
     * @return профили художников
     */
    public Page<ArtistProfileDto> getArtistsProfile(Pageable pageable) {
        Page<ArtistProfile> profiles = artistDetailsRepository.findAll(pageable);

        return profiles.map(profile -> {
            User user = profile.getUser();
            return artistMapper.toProfileResponse(user, profile);
        });
    }

    /**
     * Получает профиль художника по id
     *
     * @param id ID пользователя (художника)
     * @return профиль художника
     */
    public ArtistProfileDto getArtistProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id " + id + " не найден"));

        return getArtistProfile(user);
    }

    /**
     * Получает профиль текущего художника
     *
     * @param user пользователь (художник)
     * @return профиль художника
     */
    public ArtistProfileDto getArtistProfile(User user) {
        // Находим связанный ArtistDetails (может отсутствовать)
        ArtistProfile details = artistDetailsRepository.findByUser(user).orElse(null);
        // Преобразуем в DTO с помощью маппера
        return artistMapper.toProfileResponse(user, details);
    }

    /**
     * Создает профиль художника
     *
     * @param user    пользователь (художник)
     * @param request данные для создания профиля
     * @return созданный профиль художника
     */
    @Transactional
    public ArtistProfileDto createArtistProfile(ArtistProfileCreateDto request) {
        User user = getCurrentUser();

        // Проверяем, что профиль еще не существует
        if (artistDetailsRepository.findByUser(user).isPresent()) {
            throw new EntityDuplicateException("У художника уже создан профиль");
        }
        
        // Создаем новый ArtistDetails
        ArtistProfile details = artistMapper.toArtistDetails(request, user);
        
        // Сохраняем ArtistDetails
        ArtistProfile savedDetails = artistDetailsRepository.save(details);
        
        // Возвращаем профиль
        return artistMapper.toProfileResponse(user, savedDetails);
    }

    /**
     * Обновляет профиль художника
     *
     * @param user    пользователь (художник)
     * @param request данные для обновления профиля
     * @return обновленный профиль художника
     */
    @Transactional
    public ArtistProfileDto updateArtistProfile(ArtistProfileUpdateDto request) {
        User user = getCurrentUser();
        
        // Находим существующий ArtistDetails
        ArtistProfile details = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("У художника отсутствует профиль"));
        
        // Обновляем данные user только если нужно
        if (request.getName() != null || request.getSurname() != null) {
            artistMapper.updateUserFromRequest(request, user);
            userRepository.save(user);  // Сохраняем user в той же транзакции
        }
        
        // Обновляем ArtistDetails
        artistMapper.updateArtistDetailsFromRequest(request, details);
        
        // Сохраняем ArtistDetails
        ArtistProfile savedDetails = artistDetailsRepository.save(details);
        
        // Возвращаем обновленный профиль
        return artistMapper.toProfileResponse(user, savedDetails);
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