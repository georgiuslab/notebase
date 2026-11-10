package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.PageResponse;
import chemilabi.trade.notebase_backend.dto.UserSummaryDto;
import chemilabi.trade.notebase_backend.exception.ResourceNotFoundException;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.MaterialTag;
import chemilabi.trade.notebase_backend.model.Subject;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import chemilabi.trade.notebase_backend.model.enums.SortOption;
import chemilabi.trade.notebase_backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final MaterialTagRepository materialTagRepository;
    private final S3Service s3Service;
    private final LikeRepository likeRepository;

    public MaterialService(MaterialRepository materialRepository,
                           SubjectRepository subjectRepository,
                           UserRepository userRepository,
                           MaterialTagRepository materialTagRepository,
                           S3Service s3Service,
                           LikeRepository likeRepository) {
        this.materialRepository = materialRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.materialTagRepository = materialTagRepository;
        this.s3Service = s3Service;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public MaterialResponseDto saveMaterial(JwtAuthenticationToken token, MaterialCreateDto dto) {
        String keycloakId = token.getToken().getSubject();

        User uploader = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + keycloakId +  " not found"));

        Subject subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject with ID " + dto.subjectId() + " not found"));

        Set<MaterialTag> managedTags = new HashSet<>();
        if (dto.tagNames() != null && !dto.tagNames().isEmpty()) {
            managedTags = materialTagRepository.findAllByNameIn(dto.tagNames());
        }

        Material material = Material.builder()
                .title(dto.title())
                .description(dto.description())
                .fileUrl(dto.fileKey())
                .status(MaterialStatus.PENDING)
                .uploader(uploader)
                .subject(subject)
                .tags(managedTags)
                .build();

        Material savedMaterial = materialRepository.save(material);

        return MaterialResponseDto.fromEntity(savedMaterial, false);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getAllMaterials(String keycloakId) {
        List<Material> allMaterials = materialRepository.findAll();
        if (allMaterials.isEmpty()){
            throw new ResourceNotFoundException("No material found");
        }

        return allMaterials.stream()
                .map(material -> {
                    boolean isLiked = keycloakId != null &&
                            likeRepository.existsByUser_KeycloakIdAndMaterial_Id(keycloakId, material.getId());
                    return MaterialResponseDto.fromEntity(material, isLiked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<MaterialResponseDto> search(String q, List<String> tags,
                                                    SortOption sort, int page, int size,
                                                    String keycloakId) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort());

        Specification<Material> spec = MaterialSpecifications.isApproved()
                .and(MaterialSpecifications.matchesText(q))
                .and(MaterialSpecifications.hasAnyTag(tags));

        Page<MaterialResponseDto> result = materialRepository
                .findAll(spec, pageable)
                .map(material -> {
                    boolean isLiked = keycloakId != null &&
                            likeRepository.existsByUser_KeycloakIdAndMaterial_Id(keycloakId, material.getId());
                    return MaterialResponseDto.fromEntity(material, isLiked);
                });

        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getFavoriteMaterials(String userId) {
        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        return user.getFavorites().stream()
                .map(material -> {
                    boolean isLiked = userId != null &&
                            likeRepository.existsByUser_KeycloakIdAndMaterial_Id(userId, material.getId());
                    return MaterialResponseDto.fromEntity(material, isLiked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDto getMaterial(Long id, String keycloakId) {
        Material material = materialRepository.findByIdAndStatusNot(id, MaterialStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material with ID " + id + " not found or is awaiting approval")
                );
        String downloadUrl = s3Service.generatePresignedDownloadUrl(material.getFileUrl());

        boolean isLiked = keycloakId != null &&
                likeRepository.existsByUser_KeycloakIdAndMaterial_Id(keycloakId, id);

        Set<String> tagNames = material.getTags() != null
                ? material.getTags().stream()
                .map(MaterialTag::getName)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return new MaterialResponseDto(
                material.getId(),
                material.getTitle(),
                material.getDescription(),
                downloadUrl,
                material.getStatus().name(),
                UserSummaryDto.fromEntity(material.getUploader()),
                material.getSubject().getName(),
                material.getLikesCount(),
                isLiked,
                tagNames,
                material.getCreatedAt()
        );
    }
}
