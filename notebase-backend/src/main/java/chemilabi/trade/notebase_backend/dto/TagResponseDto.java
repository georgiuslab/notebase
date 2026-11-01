package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.MaterialTag;

public record TagResponseDto(
        Long id,
        String name
) {
    public static TagResponseDto fromEntity(MaterialTag tag) {
        return new TagResponseDto(tag.getId(), tag.getName());
    }
}