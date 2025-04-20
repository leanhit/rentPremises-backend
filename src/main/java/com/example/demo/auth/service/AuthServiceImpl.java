package com.example.demo.auth.service.impl;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.*;
import com.example.demo.auth.repository.AuthRepository;
import com.example.demo.auth.repository.UserBusinessRoleRepository;
import com.example.demo.auth.security.JwtUtils;
import com.example.demo.auth.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Check username tồn tại
        if (authRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Check email tồn tại
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        Auth auth = Auth.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(request.getSystemRole())
                .build();

        AuthInfo authInfo = AuthInfo.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE))
                .auth(auth)
                .build();

        AuthContact authContact = AuthContact.builder()
                .phone(request.getPhone())
                .address(request.getAddress())
                .auth(auth)
                .build();

        auth.setInfo(authInfo);
        auth.setContact(authContact);

        authRepository.save(auth);

        if (request.getBusinessRoles() != null) {
            request.getBusinessRoles().forEach(roleName -> {
                UserBusinessRole ubr = UserBusinessRole.builder()
                        .user(auth)
                        .businessRole(BusinessRole.valueOf(roleName))
                        .build();
                userBusinessRoleRepository.save(ubr);
            });
        }

        String token = jwtUtils.generateToken(auth.getUsername());

        return RegisterResponse.builder()
                .success(true)
                .message("Register successful")
                .token(token)
                .refreshToken(null) // nếu bạn làm refreshToken thì thêm vào
                .tokenExpiredAt(System.currentTimeMillis() + 3600 * 1000) // 1h
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Auth auth = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!auth.isActive() || !passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(auth.getUsername());

        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .refreshToken(null) // nếu có refresh thì gắn vào
                .build();
    }
}
