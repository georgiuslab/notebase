package chemilabi.trade.notebase_backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record MaterialResponseDto(
        Long id,
        String title,
        String description,
        String fileUrl,
        String status,
        UserSummaryDto uploader,
        String subjectName,
        Set<String> tags,
        LocalDateTime createdAt
) {}