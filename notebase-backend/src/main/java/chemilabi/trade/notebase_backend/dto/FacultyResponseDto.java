package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.Faculty;

public record FacultyResponseDto(
        Long id,
        String name
) {
    public static FacultyResponseDto fromEntity(Faculty faculty) {
        return new FacultyResponseDto(
                faculty.getId(),
                faculty.getName()
        );
    }
}