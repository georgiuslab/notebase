package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.MaterialTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialTagMappingRepository extends JpaRepository<MaterialTagMapping, Long> {
}