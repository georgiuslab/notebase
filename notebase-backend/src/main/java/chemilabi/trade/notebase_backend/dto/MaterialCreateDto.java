package chemilabi.trade.notebase_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class MaterialCreateDto {
    private String title;
    private String description;
    private String fileUrl;
    private Long subjectId;
    private Set<String> tagNames;
}
