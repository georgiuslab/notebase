package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.service.FavoriteService;
import chemilabi.trade.notebase_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public UserController(UserService userService,
                          FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "მომხმარებლის სინქრონიზაცია", description = "ახდენს ავტორიზირებული მომხმარებლის " +
            "სინქრონიზაციას ბაზასთან და აბრუნებს მის მონაცემებს")
    @GetMapping("/auth")
    public ResponseEntity<UserDetailsResponseDto> getAuth(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.syncUser(jwt));
    }

    @Operation(summary = "მომხმარებლის რჩეული მასალების წამოღება",
            description = "აბრუნებს მითითებული მომხმარებლის მიერ რჩეულებში დამატებულ მასალებს")
    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<List<MaterialResponseDto>> getFavoriteMaterials(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteMaterials(userId));
    }
}
