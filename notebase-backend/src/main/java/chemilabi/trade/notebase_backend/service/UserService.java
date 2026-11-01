package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.AvatarUpdateDto;
import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.dto.UserSummaryDto;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserService(UserRepository userRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#jwt.subject")
    public UserDetailsResponseDto syncUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        User u = userRepository.findByKeycloakId(keycloakId).orElseGet(() -> {
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
        String photoKey = (savedUser.getAvatar() != null)
                ? savedUser.getAvatar() : "defaults/user-profile.png";

        String avatar = s3Service.generatePresignedDownloadUrl(photoKey);

        return UserDetailsResponseDto.fromEntity(savedUser, avatar);
    }

    @Transactional
    public UserDetailsResponseDto updateAvatar(String keycloakId, AvatarUpdateDto dto) {
        User user = userRepository.findByKeycloakId(keycloakId).orElseThrow(
                () -> new UserNotFoundException("User not found with id " + keycloakId)
        );

        user.setAvatar(dto.fileKey());
        userRepository.save(user);
        String avatar = s3Service.generatePresignedDownloadUrl(dto.fileKey());
        return UserDetailsResponseDto.fromEntity(user, avatar);
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserSummaryDto::fromEntity)
                .toList();
    }
}