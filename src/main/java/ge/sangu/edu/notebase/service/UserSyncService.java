package ge.sangu.edu.notebase.service;

import ge.sangu.edu.notebase.entity.AppUser;
import ge.sangu.edu.notebase.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserSyncService {
    private final AppUserRepository repo;

    public UserSyncService(AppUserRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AppUser getOrCreate(Jwt jwt) {
        return repo.findById(jwt.getSubject()).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setKeycloakId(jwt.getSubject());
            u.setEmail(jwt.getClaimAsString("email"));
            u.setFirstName(jwt.getClaimAsString("given_name"));
            u.setLastName(jwt.getClaimAsString("family_name"));
            return repo.save(u);
        });
    }
}
