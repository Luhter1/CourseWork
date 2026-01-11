package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.ArtistMapper;
import org.itmo.isLab1.artists.dto.ArtistProfileCreateDto;
import org.itmo.isLab1.artists.dto.ArtistProfileDto;
import org.itmo.isLab1.artists.dto.ArtistProfileUpdateDto;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.repository.ArtistDetailsRepository;
import org.itmo.isLab1.common.errors.EntityDuplicateException;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final UserRepository userRepository;
    private final ArtistDetailsRepository artistDetailsRepository;
    private final ArtistMapper artistMapper;

    /**
     * Получает профиль текущего художника
     *
     * @param authentication объект аутентификации
     * @return профиль художника
     */
    public ArtistProfileDto getArtistProfile(User user) {

        // Находим связанный ArtistDetails (может отсутствовать)
        ArtistDetails details = artistDetailsRepository.findByUser(user).orElse(null);

        // Преобразуем в DTO с помощью маппера
        return artistMapper.toProfileResponse(user, details);
    }

    /**
     * Создает профиль художника
     *
     * @param authentication объект аутентификации
     * @param request        данные для обновления профиля
     * @return обновленный профиль художника
     */
    @Transactional
    public ArtistProfileDto createArtistProfile(User user, ArtistProfileCreateDto request) {
        // Находим или создаем ArtistDetails
        ArtistDetails details = artistDetailsRepository.findByUser(user)
                .orElse(null);

        if (details != null) {
            throw new EntityDuplicateException("У художника уже создан профиль");
        }
        details = artistMapper.toArtistDetails(request, user);
        
        // Сохраняем ArtistDetails
        ArtistDetails savedDetails = artistDetailsRepository.save(details);

        // Возвращаем обновленный профиль
        return artistMapper.toProfileResponse(user, savedDetails);
    }

    /**
     * Обновляет профиль художника
     *
     * @param authentication объект аутентификации
     * @param request        данные для обновления профиля
     * @return обновленный профиль художника
     */
    @Transactional
    public ArtistProfileDto updateArtistProfile(User user, ArtistProfileUpdateDto request) {
        // Находим или создаем ArtistDetails
        ArtistDetails details = artistDetailsRepository.findByUser(user)
        .orElseThrow(() -> new ResourceNotFoundException("У художника отсутствует профиль"));
        
        // Обновляем данные пользователя
        artistMapper.updateUserFromRequest(request, user);
        User savedUser = userRepository.save(user);

        details = artistDetailsRepository.findByUser(savedUser).orElse(null);
        artistMapper.updateArtistDetailsFromRequest(request, details);


        // Сохраняем ArtistDetails
        ArtistDetails savedDetails = artistDetailsRepository.save(details);

        // Возвращаем обновленный профиль
        return artistMapper.toProfileResponse(savedUser, savedDetails);
    }
}