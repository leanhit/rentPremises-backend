package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Kiểm tra username tồn tại
    @GetMapping("check-username")
    public boolean checkUsernameExists(@RequestParam String username) {
        return authService.checkUsernameExists(username);
    }

    // Kiểm tra email tồn tại
    @GetMapping("check-email")
    public boolean checkEmailExists(@RequestParam String email) {
        return authService.checkEmailExists(email);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("Received request: " + request); // In ra request
        if (request == null) {
            System.out.println("Request is null");
        } else {
            System.out.println("Request details: " + request.toString());
        }

        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{authId}/info")
    public void updateAuthInfo(@PathVariable Long authId, @RequestBody UpdateAuthInfoRequest request) {
        authService.updateAuthInfo(authId, request);
    }

    @PutMapping("/{authId}/contact")
    public void updateAuthContact(@PathVariable Long authId, @RequestBody UpdateAuthContactRequest request) {
        authService.updateAuthContact(authId, request);
    }

    @PutMapping("/{authId}/change-password")
    public void changePassword(@PathVariable Long authId, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authId, request);
    }

    @PutMapping("/{authId}/system-role")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới được đổi SystemRole
    public void changeSystemRole(@PathVariable Long authId, @RequestBody UpdateSystemRoleRequest request) {
        authService.changeSystemRole(authId, request);
    }
}
