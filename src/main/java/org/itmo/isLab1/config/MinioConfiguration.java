package org.itmo.isLab1.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация MinIO клиента
 */
@Slf4j
@Configuration
public class MinioConfiguration {

    @Value("${spring.minio.url}")
    private String url;

    @Value("${spring.minio.access-key}")
    private String accessKey;

    @Value("${spring.minio.secret-key}")
    private String secretKey;

    /**
     * Создает и конфигурирует MinIO клиент
     *
     * @return настроенный MinIO клиент
     */
    @Bean
    public MinioClient minioClient() {
        log.info("Инициализация MinIO клиента для URL: {}", url);
        
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

}