package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с медиафайлами
 * 
 * Предоставляет методы для управления медиафайлами с поддержкой
 * пагинации, сортировки и проверки принадлежности к работам
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    /**
     * Получение списка медиафайлов работы с пагинацией и сортировкой по дате создания
     * 
     * @param workId идентификатор работы
     * @param pageable параметры пагинации и сортировки
     * @return страница медиафайлов работы, отсортированных по дате создания
     */
    Page<Media> findByWorkIdOrderByCreatedAt(Long workId, Pageable pageable);
    
    /**
     * Получение медиафайла по идентификатору с проверкой принадлежности к работе
     * 
     * Этот метод обеспечивает дополнительную безопасность, проверяя что медиафайл
     * действительно принадлежит указанной работе перед возвратом результата
     * 
     * @param mediaId идентификатор медиафайла
     * @param workId идентификатор работы
     * @return Optional с медиафайлом, если найден и принадлежит указанной работе
     */
    Optional<Media> findByIdAndWorkId(Long mediaId, Long workId);
    
    /**
     * Проверка существования медиафайла у работы
     * 
     * @param mediaId идентификатор медиафайла
     * @param workId идентификатор работы
     * @return true если медиафайл существует и принадлежит работе
     */
    boolean existsByIdAndWorkId(Long mediaId, Long workId);
    
    /**
     * Получение количества медиафайлов в работе
     * 
     * @param workId идентификатор работы
     * @return количество медиафайлов в работе
     */
    long countByWorkId(Long workId);
}