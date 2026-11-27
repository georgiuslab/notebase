package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByKeycloakId(String keycloakId);

    @Query(value = """
        SELECT EXISTS(
            SELECT 1
            FROM user_favorite_materials ufm
            JOIN users u ON u.keycloak_id = ufm.user_id
            WHERE u.keycloak_id = :keycloakId
              AND ufm.material_id = :materialId
        )
        """, nativeQuery = true)
    boolean existsFavorite(@Param("keycloakId") String keycloakId, @Param("materialId") Long materialId);

    @Modifying
    @Query(value = """
        DELETE FROM user_favorite_materials ufm
        USING users u
        WHERE ufm.user_id = u.keycloak_id
          AND u.keycloak_id = :keycloakId
          AND ufm.material_id = :materialId
        """, nativeQuery = true)
    int deleteFavorite(@Param("keycloakId") String keycloakId, @Param("materialId") Long materialId);

    @Modifying
    @Query(value = """
        INSERT INTO user_favorite_materials (user_id, material_id)
        SELECT u.keycloak_id, :materialId
        FROM users u
        WHERE u.keycloak_id = :keycloakId
        ON CONFLICT (user_id, material_id) DO NOTHING
        """, nativeQuery = true)
    int insertFavorite(@Param("keycloakId") String keycloakId, @Param("materialId") Long materialId);
}