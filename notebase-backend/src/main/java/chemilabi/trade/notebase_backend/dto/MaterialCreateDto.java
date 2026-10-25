package chemilabi.trade.notebase_backend.dto;

import java.util.Set;

public record MaterialCreateDto(
        String title,
        String description,
        String fileKey,
        Long subjectId,
        Set<String> tagNames
) {}