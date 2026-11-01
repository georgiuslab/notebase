package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.MaterialTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MaterialTagRepository extends JpaRepository<MaterialTag, Long> {

    Set<MaterialTag> findAllByNameIn(Set<String> names);
}