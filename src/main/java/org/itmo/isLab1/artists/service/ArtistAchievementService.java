package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;
import org.itmo.isLab1.artists.AchievementMapper;
import org.itmo.isLab1.artists.dto.AchievementCreateDto;
import org.itmo.isLab1.artists.dto.AchievementResponseDto;
import org.itmo.isLab1.artists.dto.AchievementUpdateDto;
import org.itmo.isLab1.artists.entity.Achievement;
import org.itmo.isLab1.artists.entity.AchievementType;
import org.itmo.isLab1.artists.entity.ArtistDetails;
import org.itmo.isLab1.artists.repository.AchievementRepository;
import org.itmo.isLab1.artists.repository.ArtistDetailsRepository;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления достижениями художников.
 * Реализует бизнес-логику CRUD операций над достижениями с учетом политик безопасности.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistAchievementService {

    private final AchievementRepository achievementRepository;
    private final ArtistDetailsRepository artistDetailsRepository;
    private final AchievementMapper achievementMapper;

    /**
     * Получает все достижения художника, отсортированные по дате создания (от новых к старым).
     *
     * @param artistId ID художника
     * @return список достижений в виде DTO
     * @throws ResourceNotFoundException если художник с указанным ID не найден
     */
    public List<AchievementResponseDto> getArtistAchievements(Long artistId) {
        // Проверяем существование художника
        ArtistDetails artist = artistDetailsRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Художник с ID " + artistId + " не найден"));

        // Получаем все достижения и конвертируем в DTO
        return achievementRepository.findByArtistIdOrderByCreatedAtDesc(artistId)
                .stream()
                .map(achievementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Создает новое достижение для художника.
     * Запрещено создание достижений с типом AUTO (системные достижения).
     *
     * @param artistId  ID художника
     * @param createDto данные для создания достижения
     * @return созданное достижение в виде DTO
     * @throws PolicyViolationError      если тип достижения AUTO
     * @throws ResourceNotFoundException если художник с указанным ID не найден
     */
    @Transactional
    public AchievementResponseDto createAchievement(Long artistId, AchievementCreateDto createDto) {
        // Проверяем, что тип не AUTO
        if (createDto.getType() == AchievementType.AUTO) {
            throw new PolicyViolationError("Нельзя создавать достижения с типом AUTO");
        }

        // Находим художника
        ArtistDetails artist = artistDetailsRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Художник с ID " + artistId + " не найден"));

        // Создаем сущность через маппер
        Achievement achievement = achievementMapper.toEntity(createDto);
        achievement.setArtist(artist);

        // Сохраняем и возвращаем DTO
        Achievement savedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toResponseDto(savedAchievement);
    }

    /**
     * Обновляет существующее достижение художника.
     * Запрещено обновление достижений с типом AUTO (системные достижения).
     *
     * @param artistId      ID художника
     * @param achievementId ID достижения
     * @param updateDto     данные для обновления
     * @return обновленное достижение в виде DTO
     * @throws ResourceNotFoundException если достижение не найдено или не принадлежит художнику
     * @throws PolicyViolationError      если попытка обновить достижение с типом AUTO
     */
    @Transactional
    public AchievementResponseDto updateAchievement(Long artistId, Long achievementId, AchievementUpdateDto updateDto) {
        // Находим достижение и проверяем принадлежность художнику
        Achievement achievement = achievementRepository.findByIdAndArtistId(achievementId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Достижение с ID " + achievementId + " не найдено для художника с ID " + artistId));

        // Проверяем, что тип не AUTO (нельзя редактировать автоматические достижения)
        if (achievement.getType() == AchievementType.AUTO) {
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
        if (achievement.getType() == AchievementType.AUTO) {
            throw new PolicyViolationError("Нельзя удалять достижения с типом AUTO");
        }

        // Удаляем достижение
        achievementRepository.delete(achievement);
    }
}
