package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.PageResponse;
import chemilabi.trade.notebase_backend.exception.ResourceNotFoundException;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.MaterialTag;
import chemilabi.trade.notebase_backend.model.Subject;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import chemilabi.trade.notebase_backend.model.enums.SortOption;
import chemilabi.trade.notebase_backend.repository.MaterialRepository;
import chemilabi.trade.notebase_backend.repository.MaterialTagRepository;
import chemilabi.trade.notebase_backend.repository.SubjectRepository;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final MaterialTagRepository materialTagRepository;

    public MaterialService(MaterialRepository materialRepository,
                           SubjectRepository subjectRepository,
                           UserRepository userRepository,
                           MaterialTagRepository materialTagRepository) {
        this.materialRepository = materialRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.materialTagRepository = materialTagRepository;
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
//        if (managedTags.size() != dto.tagNames().size()) {
//            throw new ResourceNotFoundException("ერთ-ერთი მითითებული თეგი არ არსებობს სისტემაში!");
//        }

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

        return MaterialResponseDto.fromEntity(savedMaterial);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getAllMaterials() {
        List<MaterialResponseDto> allMaterials = materialRepository.findAll().stream()
                .map(MaterialResponseDto::fromEntity)
                .toList();
        if (allMaterials.isEmpty()){
            throw new ResourceNotFoundException("No material found");
        }
        return allMaterials;
    }

    @Transactional(readOnly = true)
    public PageResponse<MaterialResponseDto> search(String q, List<String> tags,
                                                    SortOption sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort());

        Specification<Material> spec = MaterialSpecifications.isApproved()
                .and(MaterialSpecifications.matchesText(q))
                .and(MaterialSpecifications.hasAnyTag(tags));

        Page<MaterialResponseDto> result = materialRepository
                .findAll(spec, pageable)
                .map(MaterialResponseDto::fromEntity);

        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getFavoriteMaterials(String userId) {
        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        return user.getFavorites().stream()
                .map(MaterialResponseDto::fromEntity)
                .toList();
    }
}
