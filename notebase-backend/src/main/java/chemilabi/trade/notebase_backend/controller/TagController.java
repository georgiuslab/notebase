package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.dto.SubjectResponseDto;
import chemilabi.trade.notebase_backend.dto.TagResponseDto;
import chemilabi.trade.notebase_backend.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> tags = tagService.getAllTags();
        if (tags.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tags);
    }
}