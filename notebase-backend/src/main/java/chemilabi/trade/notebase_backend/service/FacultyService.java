package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public List<Faculty> getAllFaculties() {
        List<Faculty> allFaculties = facultyRepository.findAll();
        if (allFaculties.isEmpty()){
            throw new RuntimeException("No Faculties Found");
        }
        return allFaculties;
    }
}