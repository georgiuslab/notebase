package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;

import java.util.List;

public interface FavoriteService {
    List<MaterialResponseDto> getFavoriteMaterials(String userId);
}
