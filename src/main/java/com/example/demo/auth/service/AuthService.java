package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);

    void updateAuthContact(Long authId, UpdateAuthContactRequest request);
    void updateAuthInfo(Long authId, UpdateAuthInfoRequest request);
    void changePassword(Long authId, ChangePasswordRequest request);
    void changeSystemRole(Long authId, UpdateSystemRoleRequest request);

    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
}
