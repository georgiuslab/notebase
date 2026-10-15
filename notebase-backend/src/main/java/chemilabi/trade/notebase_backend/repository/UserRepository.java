package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
