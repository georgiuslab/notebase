package chemilabi.trade.notebase_backend.repository;

import chemilabi.trade.notebase_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByKeycloakId(String keycloakId);
}