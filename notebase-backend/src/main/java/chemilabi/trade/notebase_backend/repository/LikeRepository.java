package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // Проверить, ставил ли пользователь лайк материалу
    boolean existsByMaterialIdAndUserKeycloakId(Long materialId, String userKeycloakId);

    // Найти лайк для удаления (анлайк)
    Optional<Like> findByMaterialIdAndUserKeycloakId(Long materialId, String userKeycloakId);

    // Посчитать общее количество лайков у материала
    long countByMaterialId(Long materialId);
}