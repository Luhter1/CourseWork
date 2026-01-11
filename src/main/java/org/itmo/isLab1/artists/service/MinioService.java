package org.itmo.isLab1.artists.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itmo.isLab1.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        try {
            ensureBucketExists();
            
            String fileName = generateFileName(file.getOriginalFilename(), artistId, workId);
            String objectName = String.format("artist-%d/work-%d/%s", artistId, workId, fileName);
            
            InputStream inputStream = file.getInputStream();
            
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            
            ObjectWriteResponse response = minioClient.putObject(args);
            
            log.info("Файл успешно загружен в MinIO: bucket={}, object={}, etag={}", 
                    bucketName, objectName, response.etag());
            
            return objectName;
            
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла в MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
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
            // Проверяем, существует ли файл
            StatObjectArgs statArgs = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            
            minioClient.statObject(statArgs);
            
            // Удаляем файл
            RemoveObjectArgs removeArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            
            minioClient.removeObject(removeArgs);
            
            log.info("Файл успешно удален из MinIO: bucket={}, object={}", bucketName, objectName);
            
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
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
    public String generatePresignedUrl(String objectName, int expiry) {
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expiry, TimeUnit.SECONDS)
                    .build();
            
            String url = minioClient.getPresignedObjectUrl(args);
            
            log.debug("Сгенерирован presigned URL для объекта: {}", objectName);
            
            return url;
            
        } catch (Exception e) {
            log.error("Ошибка при генерации presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при генерации URL для доступа к файлу: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет и при необходимости создает bucket
     *
     * @throws RuntimeException если произошла ошибка при создании bucket
     */
    private void ensureBucketExists() {
        try {
            BucketExistsArgs existsArgs = BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build();
            
            boolean bucketExists = minioClient.bucketExists(existsArgs);
            
            if (!bucketExists) {
                MakeBucketArgs makeArgs = MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build();
                
                minioClient.makeBucket(makeArgs);
                log.info("Создан новый bucket в MinIO: {}", bucketName);
            }
            
        } catch (Exception e) {
            log.error("Ошибка при проверке/создании bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при работе с bucket: " + e.getMessage(), e);
        }
    }

    /**
     * Генерирует уникальное имя файла
     *
     * @param originalFileName оригинальное имя файла
     * @param artistId идентификатор художника
     * @param workId идентификатор работы
     * @return сгенерированное имя файла
     */
    private String generateFileName(String originalFileName, Long artistId, Long workId) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String timestamp = String.valueOf(ZonedDateTime.now().toEpochSecond());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s%s", timestamp, uuid, extension);
    }
}