package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.dto.FacultyResponseDto;
import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<List<FacultyResponseDto>> getAllFaculties() {
        List<FacultyResponseDto> faculties = facultyService.getAllFaculties();
        if (faculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @Operation(summary = "მოაქვს კონკრეტული ფაკულტეტის საგნები", description = "ფაილის ატვირთვის ფორმაში, " +
            "როდესაც მომხმარებელი ირჩევს კონკრეტულ ფაკულტეტს, გვერდით სიაში ჩნდება ამ ფაკულტეტის საგნების ჩამონათვალი")
    @GetMapping("/{facultyId}/subjects")
    public ResponseEntity<List<SubjectResponseDto>> getSubjectsByFaculty(@PathVariable Long facultyId) {
        List<SubjectResponseDto> subjects = facultyService.getSubjectsByFacultyId(facultyId);
        return ResponseEntity.ok(subjects);
    }
}
