package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.dto.UserSummaryDto;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//ექსეფშენები მაქვს დასამუშავებელი!!!
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#jwt.subject")
    public UserDetailsResponseDto syncUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        User u = userRepository.findById(keycloakId).orElseGet(() -> {
            User newUser = new User();
            newUser.setKeycloakId(keycloakId);
            newUser.setEmail(jwt.getClaimAsString("email"));
            newUser.setFirstName(jwt.getClaimAsString("given_name"));
            newUser.setLastName(jwt.getClaimAsString("family_name"));
            return newUser;
        });

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");

            String userRole = "USER";

            if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("admin"))) {
                userRole = "ADMIN";
            } else if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("moderator"))) {
                userRole = "MODERATOR";
            }

            u.setRole(userRole);
        } else {
            u.setRole("USER");
        }

        User savedUser = userRepository.save(u);

        return UserDetailsResponseDto.fromEntity(savedUser);
    }

    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserSummaryDto::fromEntity)
                .toList();
    }
}