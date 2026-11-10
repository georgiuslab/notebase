package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUser_KeycloakIdAndMaterial_Id(String keycloakId, Long materialId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Like l WHERE l.user.keycloakId = :keycloakId AND l.material.id = :materialId")
    int deleteByUser_KeycloakIdAndMaterial_Id(String keycloakId, Long materialId);
}