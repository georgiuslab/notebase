package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.User;

import java.time.OffsetDateTime;

public record UserSummaryDto(
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String avatar
) {
    public static UserSummaryDto fromEntity(User user, String avatar) {
        return new UserSummaryDto(
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                avatar
        );
    }
}
