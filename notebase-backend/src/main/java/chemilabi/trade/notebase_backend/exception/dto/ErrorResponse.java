package chemilabi.trade.notebase_backend.exception.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String message,
        OffsetDateTime timestamp
) {}
