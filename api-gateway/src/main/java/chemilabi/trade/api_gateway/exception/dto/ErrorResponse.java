package chemilabi.trade.api_gateway.exception.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String message,
        String path
) {}
