package chemilabi.trade.notebase_backend.controller;

import chemilabi.trade.notebase_backend.model.User;
import chemilabi.trade.notebase_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private final UserService userService;
    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/materials")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getMaterials(@AuthenticationPrincipal Jwt jwt) {
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String email = jwt.getClaimAsString("email");

        return ResponseEntity.ok("მოგესალმები, "
                + firstName + " " + lastName
                + " (" + email + ")! ტოკენი წარმატებით შემოწმდა ბექენდში.");
    }

    @GetMapping("/auth")
    public User getAuth(@AuthenticationPrincipal Jwt jwt) {
        return userService.syncUser(jwt);
    }
}
