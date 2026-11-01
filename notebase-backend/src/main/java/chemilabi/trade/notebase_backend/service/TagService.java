package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.TagResponseDto;
import chemilabi.trade.notebase_backend.repository.MaterialTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final MaterialTagRepository tagRepository;

    public TagService(MaterialTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagResponseDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagResponseDto::fromEntity)
                .toList();
    }
}