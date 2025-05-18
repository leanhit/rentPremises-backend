package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.Auth;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserSummary toUserDto(Auth auth);
    UserDetails loadUserByUsername(String username); 

    void changePassword(Long authId, ChangePasswordRequest request);
    void updateProfile(Long authId, ProfileUpdateRequest request);
}
