package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.aws.S3Service;
import chemilabi.trade.notebase_backend.dto.MaterialCreateDto;
import chemilabi.trade.notebase_backend.dto.MaterialResponseDto;
import chemilabi.trade.notebase_backend.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final S3Service s3Service;
    private final MaterialService materialService;

    public MaterialController(S3Service s3Service,
                              MaterialService materialService) {
        this.s3Service = s3Service;
        this.materialService = materialService;
    }

    @Operation(summary = "PreSignedURL-ის გამოთხოვა ბაზიდან",
            description = "აბრუნებს ბმულს, რომლის მეშვეობითაც S3-ზე აიტვირთება ფაილი, ბმული ძალაშია 10 წუთი")
    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            Principal principal) {

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String userEmail = jwtToken.getToken().getClaimAsString("email");
        String safeEmail = userEmail != null ? userEmail.replaceAll("[^a-zA-Z0-9-_.]", "_") : "anonymous";

        String url = s3Service.generatePresignedUploadUrl(fileName, contentType, safeEmail);
        return ResponseEntity.ok(Collections.singletonMap("uploadUrl", url));
    }

    @Operation(summary = "ფაილის PreSignedURL-ით წარმატებული ატვირთვის მერე დადასტურება",
            description = "ინახავს ფაილის მონაცემებს ბაზაში")
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmUpload(@RequestBody MaterialCreateDto dto, Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        String response = materialService.saveMaterial(jwtToken, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "ყველა მასალის წამოღება", description = "მოაქვს აბსოლუტურად ყველა ატვირთული მასალა")
    @GetMapping
    public ResponseEntity<List<MaterialResponseDto>> getAllMaterials() {
        List<MaterialResponseDto> allMaterials = materialService.getAllMaterials();
        if(materialService.getAllMaterials().isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allMaterials);
    }
}