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
//ექსეფშენები მაქვს დასამუშავებელი!!!
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

    public MaterialResponseDto saveMaterial(JwtAuthenticationToken token, MaterialCreateDto dto) {
        String keycloakId = token.getToken().getSubject();

        User uploader = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Subject subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Set<MaterialTag> managedTags = dto.tagNames().stream().map(tagName ->
                materialTagRepository.findByName(tagName)
                        .orElseGet(() -> materialTagRepository.save(MaterialTag.builder().name(tagName).build()))
        ).collect(Collectors.toSet());

        Material material = Material.builder()
                .title(dto.title())
                .description(dto.description())
                .fileUrl(dto.fileUrl())
                .status(MaterialStatus.PENDING)
                .uploader(uploader)
                .subject(subject)
                .tags(managedTags)
                .build();

        Material savedMaterial = materialRepository.save(material);

        return MaterialResponseDto.fromEntity(savedMaterial);
    }

    public List<MaterialResponseDto> getAllMaterials() {
        List<MaterialResponseDto> allMaterials = materialRepository.findAll().stream()
                .map(MaterialResponseDto::fromEntity)
                .toList();
        if (allMaterials.isEmpty()){
            throw new RuntimeException("No material found");
        }
        return allMaterials;
    }
}
