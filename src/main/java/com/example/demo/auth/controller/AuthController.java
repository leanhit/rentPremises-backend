package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.service.AuthService;
import com.example.demo.auth.repository.AuthRepository;  // THÊM DÒNG NÀY
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthRepository authRepository;

    // Kiểm tra username tồn tại
    @GetMapping("check-username")
    public boolean checkUsernameExists(@RequestParam String username) {
        return authRepository.existsByUsername(username);
    }

    // Kiểm tra email tồn tại
    @GetMapping("check-email")
    public boolean checkEmailExists(@RequestParam String email) {
        return authRepository.existsByEmail(email);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
