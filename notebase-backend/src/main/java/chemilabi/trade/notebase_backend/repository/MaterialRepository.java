package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material,Long> {
}
