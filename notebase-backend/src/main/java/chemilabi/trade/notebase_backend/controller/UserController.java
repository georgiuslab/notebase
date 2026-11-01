package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.AvatarUpdateDto;
import chemilabi.trade.notebase_backend.dto.PresignedUrlResponseDto;
import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.FacultyResponseDto;
import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.dto.UserSummaryDto;
import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.service.MaterialService;
import chemilabi.trade.notebase_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;
    private final MaterialService materialService;

    public UserController(UserService userService,
                          S3Service s3Service, MaterialService materialService) {
        this.userService = userService;
        this.s3Service = s3Service;
        this.materialService = materialService;
    }

    @Operation(summary = "მომხმარებლის სინქრონიზაცია", description = "ახდენს ავტორიზირებული მომხმარებლის " +
            "სინქრონიზაციას ბაზასთან და აბრუნებს მის მონაცემებს")
    @GetMapping("/auth")
    public ResponseEntity<UserDetailsResponseDto> getAuth(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.syncUser(jwt));
    }

    @GetMapping("/upload-url")
    public ResponseEntity<PresignedUrlResponseDto> getAvatarUploadUrl(@RequestParam String avatarName,
                                                                    @RequestParam String contentType,
                                                                    @RequestParam long fileSize,
                                                                    Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String email = jwtToken.getToken().getClaimAsString("email");
        String safeEmail = email != null ? email.replaceAll("[^a-zA-Z0-9._-]", "_") : "anonymous";

        PresignedUrlResponseDto dto = s3Service
                .generatePresignedAvatarUploadUrl(avatarName, contentType, fileSize, safeEmail);

        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/update/avatar")
    public ResponseEntity<UserDetailsResponseDto> updateAvatar(@RequestBody AvatarUpdateDto dto,
                                                               Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String keycloakId = jwtToken.getToken().getSubject();

        UserDetailsResponseDto userDetailsResponseDto = userService.updateAvatar(keycloakId, dto);

        return ResponseEntity.ok(userDetailsResponseDto);
    }
    
    @Operation(summary = "მომხმარებლის რჩეული მასალების წამოღება",
            description = "აბრუნებს მითითებული მომხმარებლის მიერ რჩეულებში დამატებულ მასალებს")
    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<List<MaterialResponseDto>> getFavoriteMaterials(@PathVariable String userId) {
        return ResponseEntity.ok(materialService.getFavoriteMaterials(userId));
    }
    
    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers() {
        List <UserSummaryDto> allUsers =  userService.getAllUsers();
        if (allUsers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allUsers);
    }
}
