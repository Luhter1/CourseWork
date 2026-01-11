package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.AchievementMapper;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.itmo.isLab1.artists.entity.AchievementTypeEnum;
import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.itmo.isLab1.artists.repository.AchievementRepository;
import org.itmo.isLab1.artists.repository.ArtistProfileRepository;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления достижениями художников.
 * Реализует бизнес-логику CRUD операций над достижениями с учетом политик безопасности.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistAchievementService {

    private final AchievementRepository achievementRepository;
    private final ArtistProfileRepository artistDetailsRepository;
    private final AchievementMapper achievementMapper;
    private final UserRepository userRepository;

    /**
     * Получает все достижения художника
     *
     * @param userId ID пользователя
     * @return список достижений в виде DTO
     * @throws ResourceNotFoundException если художник с указанным ID не найден
     */
    public Page<AchievementDto> getArtistAchievements(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с id " + userId + " не найден"));

        Long artistId = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника с id " + userId + " не найден")).getId();

        // Получаем все достижения и конвертируем в DTO
        return achievementRepository.findByArtistIdOrderByCreatedAtDesc(artistId, pageable)
            .map(achievementMapper::toResponseDto);
    }

    /**
     * Получает все достижения текущего художника
     *
     * @return список достижений в виде DTO
     * @throws ResourceNotFoundException если художник с указанным ID не найден
     */
    public Page<AchievementDto> getCurrentArtistAchievements(Pageable pageable) {
        Long artistId = getCurrentArtistId();

        // Получаем все достижения и конвертируем в DTO
        return achievementRepository.findByArtistIdOrderByCreatedAtDesc(artistId, pageable)
            .map(achievementMapper::toResponseDto);
    }

    /**
     * Создает новое достижение для художника.
     * Запрещено создание достижений с типом AUTO (системные достижения).
     *
     * @param createDto данные для создания достижения
     * @return созданное достижение в виде DTO
     * @throws PolicyViolationError      если тип достижения AUTO
     * @throws ResourceNotFoundException если художник с указанным ID не найден
     */
    @Transactional
    public AchievementDto createAchievement(AchievementCreateDto createDto) {
        Long artistId = getCurrentArtistId();

        // Проверяем, что тип не AUTO
        if (createDto.getType() == AchievementTypeEnum.AUTO) {
            throw new PolicyViolationError("Нельзя создавать достижения с типом AUTO");
        }

        // Находим художника
        ArtistProfile artist = artistDetailsRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Художник с ID " + artistId + " не найден"));

        // Создаем сущность через маппер
        Achievement achievement = achievementMapper.toEntity(createDto, artist);

        // Сохраняем и возвращаем DTO
        Achievement savedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toResponseDto(savedAchievement);
    }

    /**
     * Обновляет существующее достижение художника.
     * Запрещено обновление достижений с типом AUTO (системные достижения).
     *
     * @param achievementId ID достижения
     * @param updateDto     данные для обновления
     * @return обновленное достижение в виде DTO
     * @throws ResourceNotFoundException если достижение не найдено или не принадлежит художнику
     * @throws PolicyViolationError      если попытка обновить достижение с типом AUTO
     */
    @Transactional
    public AchievementDto updateAchievement(Long achievementId, AchievementUpdateDto updateDto) {
        Long artistId = getCurrentArtistId();

        // Находим достижение и проверяем принадлежность художнику
        Achievement achievement = achievementRepository.findByIdAndArtistId(achievementId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Достижение с ID " + achievementId + " не найдено для художника с ID " + artistId));

        // Проверяем, что тип не AUTO (нельзя редактировать автоматические достижения)
        if (achievement.getType() == AchievementTypeEnum.AUTO) {
            throw new PolicyViolationError("Нельзя редактировать достижения с типом AUTO");
        }

        // Обновляем поля через маппер
        achievementMapper.updateEntityFromDto(updateDto, achievement);

        // Сохраняем и возвращаем DTO
        Achievement updatedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toResponseDto(updatedAchievement);
    }

    /**
     * Удаляет достижение художника.
     * Запрещено удаление достижений с типом AUTO (системные достижения).
     *
     * @param artistId      ID художника
     * @param achievementId ID достижения
     * @throws ResourceNotFoundException если достижение не найдено или не принадлежит художнику
     * @throws PolicyViolationError      если попытка удалить достижение с типом AUTO
     */
    @Transactional
    public void deleteAchievement(Long artistId, Long achievementId) {
        // Находим достижение и проверяем принадлежность художнику
        Achievement achievement = achievementRepository.findByIdAndArtistId(achievementId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Достижение с ID " + achievementId + " не найдено для художника с ID " + artistId));

        // Проверяем, что тип не AUTO (нельзя удалять автоматические достижения)
        if (achievement.getType() == AchievementTypeEnum.AUTO) {
            throw new PolicyViolationError("Нельзя удалять достижения с типом AUTO");
        }

        // Удаляем достижение
        achievementRepository.delete(achievement);
    }

    /**
     * Вспомогательный метод для получения ID текущего художника из контекста безопасности
     *
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException     если пользователь не является художником
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    private Long getCurrentArtistId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Извлекаем текущего пользователя из SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username " + username + " не найден"));

        // Находим связанный ArtistDetails
        ArtistProfile artistDetails = artistDetailsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Профиль художника не найден"));

        return artistDetails.getId();
    }
}
