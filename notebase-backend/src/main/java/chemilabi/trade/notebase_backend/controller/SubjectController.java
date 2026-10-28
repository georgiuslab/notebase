package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.model.Subject;
import chemilabi.trade.notebase_backend.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        if (subjects.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subjects);
    }
}