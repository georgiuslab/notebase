package ge.sangu.edu.notebase.repository;

import ge.sangu.edu.notebase.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

}