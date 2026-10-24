package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.User;

import java.time.OffsetDateTime;

public record UserDetailsResponseDto (
    String keycloakId,
    String firstName,
    String lastName,
    String email,
    String role,
    Boolean isDeleted,
    OffsetDateTime createdAt
){
    public static UserDetailsResponseDto fromEntity(User user) {
        return new UserDetailsResponseDto(
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getIsDeleted(),
                user.getCreatedAt()
        );
    }
}
