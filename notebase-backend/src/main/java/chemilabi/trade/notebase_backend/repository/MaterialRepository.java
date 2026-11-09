package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material,Long>, JpaSpecificationExecutor<Material> {

    @Modifying(clearAutomatically = true)
    @Query("update Material m set m.likesCount = m.likesCount + 1 where m.id = :id")
    void incrementLikes(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update Material m set m.likesCount = m.likesCount - 1 " +
            "where m.id = :id and m.likesCount > 0")
    void decrementLikes(@Param("id") Long id);

    List<Material> findByStatusNot(MaterialStatus status);

    Optional<Material> findByIdAndStatusNot(Long id, MaterialStatus status);
}
