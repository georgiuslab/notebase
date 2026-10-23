package chemilabi.trade.notebase_backend.dto;

public record UserSummaryDto(
        String keycloakId,
        String firstName,
        String lastName,
        String email
) {}
