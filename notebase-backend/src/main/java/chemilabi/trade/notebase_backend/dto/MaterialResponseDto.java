package chemilabi.trade.notebase_backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialResponseDto {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String status;
    private String uploaderEmail;
    private String subjectName;
    private Set<String> tags;
    private LocalDateTime createdAt;
}