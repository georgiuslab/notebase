package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.FacultyResponseDto;
import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.exception.ResourceNotFoundException;
import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public List<FacultyResponseDto> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(FacultyResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsByFacultyId(Long facultyId) {
        Faculty faculty = facultyRepository.findByIdWithSubjects(facultyId).orElseThrow(
                () -> new ResourceNotFoundException("ფაკულტეტი აიდით " +  facultyId + " ვერ მოიძებნა")
        );
        return faculty.getSubjects().stream()
                .map(SubjectResponseDto::fromEntity)
                .toList();
    }
}