package com.example.demo.auth.utils;

import com.example.demo.auth.entity.Auth;
import com.example.demo.auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    @Autowired
    private AuthRepository authRepository;

    public Auth getCurrentAuth() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
