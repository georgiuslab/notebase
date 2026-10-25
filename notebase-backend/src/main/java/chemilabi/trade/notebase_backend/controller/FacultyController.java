package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.model.Faculty;
import chemilabi.trade.notebase_backend.service.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        List<Faculty> allFaculties =  facultyService.getAllFaculties();
        return ResponseEntity.ok().body(allFaculties);
    }
}