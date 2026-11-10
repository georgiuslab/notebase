package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.exception.ResourceNotFoundException;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.model.Like;
import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import chemilabi.trade.notebase_backend.repository.LikeRepository;
import chemilabi.trade.notebase_backend.repository.MaterialRepository;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    @Transactional
    public void like(String keycloakId, Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material with id: " + materialId +
                        " not found or is awaiting approval"));

        if(!material.getStatus().equals(MaterialStatus.APPROVED)){
            throw new ResourceNotFoundException("Material with id: " + materialId +
                    " not found or is awaiting approval");
        }

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + keycloakId + " not found"));

        if (likeRepository.existsByUser_KeycloakIdAndMaterial_Id(keycloakId, materialId)) {
            likeRepository.deleteByUser_KeycloakIdAndMaterial_Id(keycloakId, materialId);
            materialRepository.decrementLikes(materialId);
        } else {
            likeRepository.saveAndFlush(new Like(material, user));
            materialRepository.incrementLikes(materialId);
        }
    }
}
