package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.PageResponse;
import chemilabi.trade.notebase_backend.dto.PresignedUrlResponseDto;
import chemilabi.trade.notebase_backend.exception.UserNotFoundException;
import chemilabi.trade.notebase_backend.model.enums.SortOption;
import chemilabi.trade.notebase_backend.service.FavoriteService;
import chemilabi.trade.notebase_backend.service.LikeService;
import chemilabi.trade.notebase_backend.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/materials")
public class MaterialController {

    private final S3Service s3Service;
    private final MaterialService materialService;
    private final LikeService likeService;
    private final FavoriteService favoriteService;

    @Operation(summary = "PreSignedURL-ის გამოთხოვა",
            description = "აბრუნებს ბმულს, რომლის მეშვეობითაც S3-ზე აიტვირთება ფაილი, ბმული ძალაშია 10 წუთი")
    @GetMapping("/upload-url")
    public ResponseEntity<PresignedUrlResponseDto> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestParam long fileSize,
            Principal principal) {

        String safeEmail = extractSafeEmail(principal);
        PresignedUrlResponseDto url =
                s3Service.generatePresignedUploadUrl(fileName, contentType, fileSize, safeEmail);
        return ResponseEntity.ok(url);
    }

    @Operation(summary = "ფაილის წარმატებული ატვირთვის დადასტურება",
            description = "ამოწმებს ფაილს S3-ზე და ინახავს მონაცემებს ბაზაში")
    @PostMapping("/confirm")
    public ResponseEntity<MaterialResponseDto> confirmUpload(@RequestBody MaterialCreateDto dto,
                                                             Principal principal) {
        String safeEmail = extractSafeEmail(principal);
        s3Service.verifyUploadedPdf(dto.fileKey(), safeEmail);

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        MaterialResponseDto response = materialService.saveMaterial(jwtToken, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "ყველა მასალის წამოღება",
            description = "მოაქვს ყველა ატვირთული მასალა")
    @GetMapping
    public ResponseEntity<List<MaterialResponseDto>> getAllMaterials(Principal principal) {
        String keycloakId = getKeycloakId(principal);

        return ResponseEntity.ok(materialService.getAllMaterials(keycloakId));
    }

    @Operation(summary = "მასალის ძებნა სათაურის, აღწერის, თეგების, თარიღისა და რეიტინგის მიხედვით",
            description = "გადაეცემა სათაური/აღწერა, თეგები, სორტირების ოფციები (newest/NEWEST, likes/LIKES)," +
                    " გვერდი და გვერდზე მასალების რაოდენობა")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<MaterialResponseDto>> search(
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(required = false) List<@Size(max = 50) String> tags,
            @RequestParam(defaultValue = "NEWEST") String sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size,
            Principal principal) {

        String keycloakId = getKeycloakId(principal);

        SortOption upperCase = SortOption.valueOf(sort.toUpperCase());
        return ResponseEntity.ok(materialService.search(q, tags, upperCase, page, size, keycloakId));
    }

    @Operation(summary = "კონკრეტული მასალის წამოღება",
            description = "გადაეცემა მასალის ID და მოაქვს შესაბამისი მასალა")
    @GetMapping("{id}")
    public ResponseEntity<MaterialResponseDto> getMaterial(@PathVariable Long id,
                                                           Principal principal) {
        String keycloakId = getKeycloakId(principal);
        MaterialResponseDto responseDto = materialService.getMaterial(id, keycloakId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "კონკრეტული მასალის მოწონებულებში დამატება/წაშლა",
            description = "თუ მასალა მოწონებულებში არაა დამატებული, ამატებს, თუ უკვე არსებობს - წაშლის")
    @PostMapping("/{materialId}/like")
    public ResponseEntity<Void> likeMaterial(@PathVariable("materialId") Long materialId,
                                             Principal principal) {
        String keycloakId = getKeycloakId(principal);
        likeService.like(keycloakId, materialId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "კონკრეტული მასალის ფავორიტებში დამატება/წაშლა",
            description = "თუ მასალა ფავორიტებში არაა დამატებული, ამატებს, თუ უკვე არსებობს - წაშლის")
    @PostMapping("/{materialId}/favorite")
    public ResponseEntity<Void> addToFavorites(@PathVariable("materialId") Long materialId,
                                               Principal principal) {
        String keycloakId = getKeycloakId(principal);
        favoriteService.toggleFavorites(keycloakId, materialId);
        return ResponseEntity.ok().build();
    }

    private String extractSafeEmail(Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String email = jwtToken.getToken().getClaimAsString("email");
        return email != null ? email.replaceAll("[^a-zA-Z0-9._-]", "_") : "anonymous";
    }

    private String getKeycloakId(Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String keycloakId = jwtToken.getToken().getSubject();

        if(keycloakId == null){
            throw new UserNotFoundException("Not Authenticated");
        }

        return keycloakId;
    }
}