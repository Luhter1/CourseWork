package org.itmo.isLab1.artists.repository;

import org.itmo.isLab1.artists.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    
    /**
     * Получение работ художника с пагинацией
     *
     * @param artistId идентификатор художника
     * @param pageable параметры пагинации
     * @return страница работ художника
     */
    Page<Work> findByArtistId(Long artistId, Pageable pageable);
    
    /**
     * Поиск работы по ID с проверкой принадлежности художнику
     *
     * @param id идентификатор работы
     * @param artistId идентификатор художника
     * @return Optional с работой, если найдена и принадлежит художнику
     */
    Optional<Work> findByIdAndArtistId(Long id, Long artistId);
    
    /**
     * Проверка существования работы у художника
     *
     * @param id идентификатор работы
     * @param artistId идентификатор художника
     * @return true если работа существует и принадлежит художнику
     */
    boolean existsByIdAndArtistId(Long id, Long artistId);
}
