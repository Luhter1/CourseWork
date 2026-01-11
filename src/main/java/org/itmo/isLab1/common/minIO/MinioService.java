package org.itmo.isLab1.common.minIO;

import io.minio.*;
import io.minio.http.Method;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с файловым хранилищем MinIO
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${spring.minio.bucket-name}")
    private String bucketName;

    /**
     * Загружает файл в MinIO
     *
     * @param file файл для загрузки
     * @param artistId идентификатор художника
     * @param workId идентификатор работы
     * @return путь к загруженному файлу
     * @throws RuntimeException если произошла ошибка при загрузке
     */
    public String uploadFile(MultipartFile file, Long artistId, Long workId) {
        String objectName = String.format(
                "artist-%d/work-%d/%s",
                artistId,
                workId,
                generateFileName(file.getOriginalFilename())
        );

        try (InputStream inputStream = file.getInputStream()) {

            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info(
                    "Файл успешно загружен в MinIO: bucket={}, object={}, etag={}",
                    bucketName,
                    objectName,
                    response.etag()
            );

            return objectName;

        } catch (Exception e) {
            log.error("Ошибка при загрузке файла в MinIO", e);
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

    /**
     * Удаляет файл из MinIO
     *
     * @param objectName путь к файлу в MinIO
     * @throws ResourceNotFoundException если файл не найден
     * @throws RuntimeException если произошла ошибка при удалении
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                       .build()
            );
            log.info("Файл успешно удален из MinIO: bucket={}, object={}", bucketName, objectName);
            
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new ResourceNotFoundException("Файл не найден в хранилище: " + objectName);
            }
            log.error("Ошибка при удалении файла из MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        }
    }

    /**
     * Генерирует предварительно подписанный URL для доступа к файлу
     *
     * @param objectName путь к файлу в MinIO
     * @param expiry время жизни URL в секундах
     * @return предварительно подписанный URL
     * @throws RuntimeException если произошла ошибка при генерации URL
     */
    public String generatePresignedUrl(String objectName) {
        ensureObjectExists(objectName);

        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(86400, TimeUnit.SECONDS)
                    .build();
            
            String url = minioClient.getPresignedObjectUrl(args);
            
            log.debug("Сгенерирован presigned URL для объекта: {}", objectName);
            
            return url;
            
        } catch (Exception e) {
            log.error("Ошибка при генерации presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при генерации URL для доступа к файлу: " + e.getMessage(), e);
        }
    }

    private void ensureObjectExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new ResourceNotFoundException("Файл не найден: " + objectName);
            }
            throw new RuntimeException("Ошибка проверки существования файла", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка проверки существования файла", e);
        }
    }

    /**
     * Генерирует уникальное имя файла
     *
     * @param originalFileName оригинальное имя файла
     * @return сгенерированное имя файла
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String timestamp = String.valueOf(ZonedDateTime.now().toEpochSecond());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s%s", timestamp, uuid, extension);
    }
}