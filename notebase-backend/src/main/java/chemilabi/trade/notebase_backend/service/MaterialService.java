package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.UserSummaryDto;
import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.MaterialTag;
import chemilabi.trade.notebase_backend.model.Subject;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import chemilabi.trade.notebase_backend.repository.MaterialRepository;
import chemilabi.trade.notebase_backend.repository.MaterialTagRepository;
import chemilabi.trade.notebase_backend.repository.SubjectRepository;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

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

    public MaterialService(MaterialRepository materialRepository,
                           SubjectRepository subjectRepository,
                           UserRepository userRepository,
                           MaterialTagRepository materialTagRepository) {
        this.materialRepository = materialRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.materialTagRepository = materialTagRepository;
    }

    public String saveMaterial(JwtAuthenticationToken token, MaterialCreateDto dto) {
        String keycloakId = token.getToken().getSubject();

        User uploader = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UsernameNotFoundException("იუზერი ვერ მოიძებნა"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("საგანი ვერ მოიძებნა"));

        Set<MaterialTag> managedTags = dto.getTagNames().stream().map(tagName ->
                materialTagRepository.findByName(tagName)
                        .orElseGet(() -> materialTagRepository.save(MaterialTag.builder().name(tagName).build()))
        ).collect(Collectors.toSet());

        Material material = Material.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .fileUrl(dto.getFileUrl())
                .status(MaterialStatus.PENDING)
                .uploader(uploader)
                .subject(subject)
                .tags(managedTags)
                .build();

        materialRepository.save(material);

        return "მასალა წარმატებით აიტვირთდა და ელოდება მოდერაციას!";
    }

    public List<MaterialResponseDto> getAllMaterials() {
        return materialRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

private MaterialResponseDto convertToDto(Material material) {
    return new MaterialResponseDto(
            material.getId(),
            material.getTitle(),
            material.getDescription(),
            material.getFileUrl(),
            material.getStatus() != null ? material.getStatus().name() : null,

            material.getUploader() != null ? new UserSummaryDto(
                    material.getUploader().getKeycloakId(),
                    material.getUploader().getFirstName(),
                    material.getUploader().getLastName(),
                    material.getUploader().getEmail()
            ) : null,

            material.getSubject() != null ? material.getSubject().getName() : null,

            material.getTags() != null ? material.getTags().stream()
                    .map(MaterialTag::getName)
                    .collect(Collectors.toSet()) : new HashSet<>(),

            material.getCreatedAt()
    );
}
}
