package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

}