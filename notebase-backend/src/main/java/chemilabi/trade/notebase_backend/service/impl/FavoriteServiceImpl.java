package chemilabi.trade.notebase_backend.service.impl;

import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.repository.UserRepository;
import chemilabi.trade.notebase_backend.service.FavoriteService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final UserRepository userRepository;

    public FavoriteServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<MaterialResponseDto> getFavoriteMaterials(String userId) {
        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        return user.getFavorites().stream()
                .map(MaterialResponseDto::fromEntity)
                .toList();
    }
}
