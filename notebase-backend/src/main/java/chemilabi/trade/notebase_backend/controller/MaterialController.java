package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.dto.PresignedUrlResponseDto;
import chemilabi.trade.notebase_backend.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final S3Service s3Service;
    private final MaterialService materialService;

    public MaterialController(S3Service s3Service, MaterialService materialService) {
        this.s3Service = s3Service;
        this.materialService = materialService;
    }

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
    public ResponseEntity<List<MaterialResponseDto>> getAllMaterials() {
        return ResponseEntity.ok(materialService.getAllMaterials());
    }

    private String extractSafeEmail(Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String email = jwtToken.getToken().getClaimAsString("email");
        return email != null ? email.replaceAll("[^a-zA-Z0-9._-]", "_") : "anonymous";
    }
}