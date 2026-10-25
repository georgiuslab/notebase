package chemilabi.trade.notebase_backend.dto;

public record PresignedUrlResponseDto(
        String uploadUrl,
        String fileKey
) {
}
