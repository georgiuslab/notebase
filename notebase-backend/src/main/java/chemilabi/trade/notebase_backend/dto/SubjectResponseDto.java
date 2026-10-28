package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.Subject;

public record SubjectResponseDto(
        Long id,
        String name,
        String facultyName
) {
    public static SubjectResponseDto fromEntity(Subject subject) {
        return new SubjectResponseDto(
                subject.getId(),
                subject.getName(),
                subject.getFaculty() != null ? subject.getFaculty().getName() : null
        );
    }
}