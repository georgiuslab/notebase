package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.MaterialTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialTagRepository extends JpaRepository<MaterialTag,Long> {
    Optional<MaterialTag> findByName(String tagName);
}
