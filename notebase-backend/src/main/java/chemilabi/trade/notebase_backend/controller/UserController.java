package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.AvatarUpdateDto;
import chemilabi.trade.notebase_backend.dto.PresignedUrlResponseDto;
import chemilabi.trade.notebase_backend.dto.UserDetailsResponseDto;
import chemilabi.trade.notebase_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    public UserController(UserService userService,
                          S3Service s3Service) {
        this.userService = userService;
        this.s3Service = s3Service;
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
                                                               Principal principal){
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String keycloakId = jwtToken.getToken().getSubject();

        UserDetailsResponseDto userDetailsResponseDto = userService.updateAvatar(keycloakId,dto);

        return ResponseEntity.ok(userDetailsResponseDto);
    }
}
