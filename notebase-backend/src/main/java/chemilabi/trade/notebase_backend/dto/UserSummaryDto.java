package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.User;

import java.time.OffsetDateTime;

public record UserSummaryDto(
        String keycloakId,
        String firstName,
        String lastName,
        String email
) {
    public static UserSummaryDto fromEntity(User user){
        return new UserSummaryDto(
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }
}
