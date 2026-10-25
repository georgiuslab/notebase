package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.model.Subject;
import chemilabi.trade.notebase_backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> allSubjects = subjectRepository.findAll();
        if (allSubjects.isEmpty()){
            throw new RuntimeException("No Subject found");
        }
        return allSubjects;
    }
}