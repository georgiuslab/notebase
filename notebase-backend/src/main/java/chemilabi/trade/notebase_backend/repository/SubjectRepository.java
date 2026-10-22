package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
