package sme.hub.application.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sme.hub.business.dto.CreateUsersRequest;
import sme.hub.business.services.UsersService;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class AuthController {

    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUsersRequest request) {
        usersService.register(request);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("refresh_token") String refreshToken) {
        usersService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}
