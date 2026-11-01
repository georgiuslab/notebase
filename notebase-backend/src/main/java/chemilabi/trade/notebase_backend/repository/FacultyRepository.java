package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty,Long> {

    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.subjects WHERE f.id = :id")
    Optional<Faculty> findByIdWithSubjects(@Param("id") Long id);
}
