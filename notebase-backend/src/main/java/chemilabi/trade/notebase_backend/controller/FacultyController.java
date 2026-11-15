package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.dto.FacultyCreateDto;
import chemilabi.trade.notebase_backend.dto.FacultyResponseDto;
import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "ახალი ფაკულტეტის დამატება", description = "ამატებს ახალ ფაკულტეტს ან აღადგენს ადრე წაშლილს")
    @PostMapping
    public ResponseEntity<FacultyResponseDto> createFaculty(@RequestBody FacultyCreateDto dto) {
        FacultyResponseDto created = facultyService.createFaculty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "ფაკულტეტის წაშლა (Soft Delete)", description = "ცვლის isDeleted ველს true-ზე")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build(); //204
    }

    @Operation(summary = "მოაქვს კონკრეტული ფაკულტეტის საგნები", description = "ფაილის ატვირთვის ფორმაში, " +
            "როდესაც მომხმარებელი ირჩევს კონკრეტულ ფაკულტეტს, გვერდით სიაში ჩნდება ამ ფაკულტეტის საგნების ჩამონათვალი")
    @GetMapping("/{facultyId}/subjects")
    public ResponseEntity<List<SubjectResponseDto>> getSubjectsByFaculty(@PathVariable Long facultyId) {
        List<SubjectResponseDto> subjects = facultyService.getSubjectsByFacultyId(facultyId);
        return ResponseEntity.ok(subjects);
    }
}
