package chemilabi.trade.notebase_backend.dto;

import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.MaterialTag;
import chemilabi.trade.notebase_backend.repository.LikeRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record MaterialResponseDto(
        Long id,
        String title,
        String description,
        String fileUrl,
        String status,
        UserSummaryDto uploader,
        String subjectName,
        int likesCount,
        boolean isLikedByCurrentUser,
        Set<String> tags,
        LocalDateTime createdAt
) {
    public static MaterialResponseDto fromEntity(Material material,
                                                 boolean isLikedByCurrentUser,
                                                 String avatar
                                                 ) {
        Set<String> tagNames = material.getTags() != null
                ? material.getTags().stream()
                .map(MaterialTag::getName)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return new MaterialResponseDto(
                material.getId(),
                material.getTitle(),
                material.getDescription(),
                material.getFileUrl(),
                material.getStatus().name(),
                UserSummaryDto.fromEntity(material.getUploader(), avatar),
                material.getSubject().getName(),
                material.getLikesCount(),
                isLikedByCurrentUser,
                tagNames,
                material.getCreatedAt()
        );
    }
}