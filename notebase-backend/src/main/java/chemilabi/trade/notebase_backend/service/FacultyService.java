package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.FacultyCreateDto;
import chemilabi.trade.notebase_backend.dto.FacultyResponseDto;
import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.exception.ResourceNotFoundException;
import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // ახალი ფაკულტეტის დამატება (ან ძველი წაშლილის აღდგენა)
    @Transactional
    public FacultyResponseDto createFaculty(FacultyCreateDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("ფაკულტეტის სახელი არ უნდა იყოს ცარიელი");
        }

        Optional<Faculty> existingFaculty = facultyRepository.findByNameIgnoreCase(dto.name().trim());

        if (existingFaculty.isPresent()) {
            Faculty faculty = existingFaculty.get();
            if (faculty.isDeleted()) {
                // თუ ადრე წაშლილი იყო, უბრალოდ აღვადგენთ
                faculty.setDeleted(false);
                return FacultyResponseDto.fromEntity(facultyRepository.save(faculty));
            } else {
                throw new IllegalArgumentException("ასეთი ფაკულტეტი უკვე არსებობს");
            }
        }

        Faculty newFaculty = new Faculty();
        newFaculty.setName(dto.name().trim());
        newFaculty.setDeleted(false);

        Faculty savedFaculty = facultyRepository.save(newFaculty);
        return FacultyResponseDto.fromEntity(savedFaculty);
    }

    // Soft Delete: ბაზიდან არ ვშლით, უბრალოდ ვნიშნავთ როგორც წაშლილს
    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ფაკულტეტი ID-ით " + id + " ვერ მოიძებნა"));

        if (faculty.isDeleted()) {
            throw new ResourceNotFoundException("ფაკულტეტი უკვე წაშლილია");
        }

        faculty.setDeleted(true);
        facultyRepository.save(faculty);
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