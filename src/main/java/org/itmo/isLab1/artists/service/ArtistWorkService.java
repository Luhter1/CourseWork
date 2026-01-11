package org.itmo.isLab1.artists.service;

import lombok.RequiredArgsConstructor;

import org.itmo.isLab1.artists.dto.*;
import org.itmo.isLab1.artists.entity.ArtistProfile;
import org.itmo.isLab1.artists.entity.Media;
import org.itmo.isLab1.artists.entity.Work;
import org.itmo.isLab1.artists.mapper.MediaMapper;
import org.itmo.isLab1.artists.mapper.WorkMapper;
import org.itmo.isLab1.artists.repository.ArtistProfileRepository;
import org.itmo.isLab1.artists.repository.MediaRepository;
import org.itmo.isLab1.artists.repository.WorkRepository;
import org.itmo.isLab1.common.errors.PolicyViolationError;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.itmo.isLab1.common.minIO.MinioService;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistWorkService {

    private final WorkRepository workRepository;
    private final ArtistProfileRepository artistDetailsRepository;
    private final UserRepository userRepository;
    private final WorkMapper workMapper;
    
    // Новые зависимости для работы с медиафайлами
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final MinioService minioService;
    
    @Value("${spring.minio.media.max-file-size:10485760}")
    private long maxFileSize;
    
    @Value("${spring.minio.media.max-files-count:10}")
    private int maxFilesCount;
    
    // Разрешенные MIME типы для загрузки
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png"
    );
    
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
        "video/mp4"
    );

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
     * Получает список медиафайлов для указанной работы с проверкой принадлежности художнику.
     *
     * @param artistId ID художника
     * @param workId ID работы
     * @param pageable параметры пагинации
     * @return страница медиафайлов в виде DTO с presigned URLs
     * @throws ResourceNotFoundException если работа не найдена или не принадлежит художнику
     */
    public Page<MediaDto> getWorkMedias(Long artistId, Long workId, Pageable pageable) {
        // Проверяем принадлежность работы художнику
        validateWorkOwnership(artistId, workId);
        
        // Получаем медиафайлы с пагинацией
        Page<Media> mediaPage = mediaRepository.findByWorkIdOrderByCreatedAt(workId, pageable);
        
        // Конвертируем в DTO и добавляем presigned URLs
        return mediaPage.map(media -> {
            MediaDto dto = mediaMapper.toDto(media);
            // Генерируем presigned URL для доступа к файлу (время жизни 1 час)
            String presignedUrl = minioService.generatePresignedUrl(media.getUri(), 3600);
            // Создаем новый DTO с обновленным URI
            return new MediaDto(dto.getId(), presignedUrl, dto.getMediaType(),
                               dto.getFileSize());
        });
    }

    /**
     * Загружает медиафайлы для указанной работы.
     *
     * @param artistId ID художника
     * @param workId ID работы
     * @param files массив файлов для загрузки
     * @return список DTO загруженных медиафайлов
     * @throws ResourceNotFoundException если работа не найдена или не принадлежит художнику
     * @throws PolicyViolationError если файлы не прошли валидацию
     */
    @Transactional
    public List<MediaDto> uploadMedia(Long artistId, Long workId, MultipartFile[] files) {
        // Проверяем принадлежность работы художнику
        Work work = validateWorkOwnership(artistId, workId);
        
        // Валидируем файлы
        validateFiles(files, workId);
        
        List<MediaDto> uploadedMedia = new ArrayList<>();
        
        try {
            for (MultipartFile file : files) {
                // Загружаем файл в MinIO
                String objectName = minioService.uploadFile(file, artistId, workId);
                
                // Определяем тип медиафайла
                MediaTypeEnum mediaType = determineMediaType(file.getContentType());
                
                // Создаем запись в базе данных
                Media media = Media.builder()
                        .work(work)
                        .uri(objectName)
                        .mediaType(mediaType)
                        .fileSize(file.getSize())
                        .build();
                
                Media savedMedia = mediaRepository.save(media);
                
                // Генерируем presigned URL для ответа
                String presignedUrl = minioService.generatePresignedUrl(objectName, 3600);
                MediaDto dto = mediaMapper.toDto(savedMedia);
                MediaDto responseDto = new MediaDto(dto.getId(), presignedUrl, dto.getMediaType(),
                                                  dto.getFileSize());
                
                uploadedMedia.add(responseDto);
            }
            
            return uploadedMedia;
            
        } catch (Exception e) {
            // В случае ошибки откатываем уже загруженные файлы
            rollbackUploadedFiles(uploadedMedia);
            throw e;
        }
    }

    /**
     * Удаляет медиафайл.
     *
     * @param artistId ID художника
     * @param workId ID работы
     * @param mediaId ID медиафайла
     * @throws ResourceNotFoundException если медиафайл не найден или не принадлежит работе/художнику
     */
    @Transactional
    public void deleteMedia(Long artistId, Long workId, Long mediaId) {
        // Проверяем принадлежность работы художнику
        validateWorkOwnership(artistId, workId);
        
        // Находим медиафайл с проверкой принадлежности к работе
        Media media = mediaRepository.findByIdAndWorkId(mediaId, workId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Медиафайл с ID " + mediaId + " не найден для работы " + workId));
        
        try {
            // Удаляем файл из MinIO
            minioService.deleteFile(media.getUri());
            
            // Удаляем запись из базы данных
            mediaRepository.delete(media);
            
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении медиафайла: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет принадлежность работы указанному художнику.
     *
     * @param artistId ID художника
     * @param workId ID работы
     * @return работа, если проверка прошла успешно
     * @throws ResourceNotFoundException если работа не найдена или не принадлежит художнику
     */
    private Work validateWorkOwnership(Long artistId, Long workId) {
        return workRepository.findByIdAndArtistId(workId, artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Работа с ID " + workId + " не найдена для художника " + artistId));
    }

    /**
     * Валидирует загружаемые файлы.
     *
     * @param files массив файлов
     * @param workId ID работы
     * @throws PolicyViolationError если файлы не прошли валидацию
     */
    private void validateFiles(MultipartFile[] files, Long workId) {
        if (files == null || files.length == 0) {
            throw new PolicyViolationError("Необходимо выбрать файлы для загрузки");
        }
        
        // Проверяем количество файлов
        if (files.length > maxFilesCount) {
            throw new PolicyViolationError("Можно загрузить не более " + maxFilesCount + " файлов за раз");
        }
        
        // Проверяем существующее количество медиафайлов
        long currentMediaCount = mediaRepository.countByWorkId(workId);
        if (currentMediaCount + files.length > maxFilesCount) {
            throw new PolicyViolationError("Общее количество медиафайлов не может превышать " + maxFilesCount);
        }
        
        for (MultipartFile file : files) {
            validateSingleFile(file);
        }
    }

    /**
     * Валидирует отдельный файл.
     *
     * @param file файл для валидации
     * @throws PolicyViolationError если файл не прошел валидацию
     */
    private void validateSingleFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PolicyViolationError("Файл не должен быть пустым");
        }
        
        // Проверяем размер файла
        if (file.getSize() > maxFileSize) {
            throw new PolicyViolationError("Размер файла не должен превышать " +
                    (maxFileSize / 1024 / 1024) + " МБ");
        }
        
        // Проверяем тип файла
        String contentType = file.getContentType();
        if (contentType == null || (!ALLOWED_IMAGE_TYPES.contains(contentType) &&
                                   !ALLOWED_VIDEO_TYPES.contains(contentType))) {
            throw new PolicyViolationError("Поддерживаются только файлы типов: JPEG, PNG, MP4");
        }
    }

    /**
     * Определяет тип медиафайла на основе MIME типа.
     *
     * @param contentType MIME тип файла
     * @return тип медиафайла
     */
    private MediaTypeEnum determineMediaType(String contentType) {
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return MediaTypeEnum.IMAGE;
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            return MediaTypeEnum.VIDEO;
        }
        throw new PolicyViolationError("Неподдерживаемый тип файла: " + contentType);
    }

    /**
     * Откатывает загруженные файлы в случае ошибки.
     *
     * @param uploadedMedia список уже загруженных медиафайлов
     */
    private void rollbackUploadedFiles(List<MediaDto> uploadedMedia) {
        for (MediaDto media : uploadedMedia) {
            try {
                mediaRepository.deleteById(media.getId());
                minioService.deleteFile(media.getUri());
            } catch (Exception rollbackException) {
                // Логируем ошибку отката, но не прерываем процесс
                // В реальном приложении здесь должна быть более сложная логика восстановления
            }
        }
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

    /**
     * Получает ID текущего художника из контекста безопасности (публичный метод для контроллера).
     *
     * @return ID художника
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws ResourceNotFoundException если профиль художника не найден
     */
    public Long getCurrentArtistIdForController() {
        return getCurrentArtistId();
    }
}
