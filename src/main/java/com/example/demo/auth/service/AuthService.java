package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.Auth;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserSummary toUserDto(Auth auth);
    UserDetails loadUserByUsername(String username); 

    void updateAuthContact(Long authId, UpdateAuthContactRequest request);
    void updateAuthInfo(Long authId, UpdateAuthInfoRequest request);
    void changePassword(Long authId, ChangePasswordRequest request);
    void changeRole(Long authId, UpdateRoleRequest request);
    void updateProfile(Long authId, ProfileUpdateRequest request);
    
    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
}
