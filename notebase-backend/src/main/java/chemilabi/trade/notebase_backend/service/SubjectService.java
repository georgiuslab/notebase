package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(SubjectResponseDto::fromEntity)
                .toList();
    }
}