package ge.sangu.edu.notebase.controller;

import ge.sangu.edu.notebase.entity.AppUser;
import ge.sangu.edu.notebase.service.UserSyncService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    private final UserSyncService sync;

    public TestController(UserSyncService sync) { this.sync = sync; }

    @GetMapping("/sync")
    public AppUser me(@AuthenticationPrincipal Jwt jwt) {
        return sync.getOrCreate(jwt);
    }

    @GetMapping("/moderation/queue")
    public String queue() { return "მხოლოდ მოდერატორისთვის"; }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ping")
    public String admin() { return "მხოლოდ ადმინისთვის"; }

    @GetMapping("/public/test")
    public String publicTest() {
        return "API მუშაობს წარმატებით!";
    }
}
