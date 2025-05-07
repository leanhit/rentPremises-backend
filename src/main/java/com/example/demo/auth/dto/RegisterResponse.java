package com.example.demo.auth.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RegisterResponse {
    private boolean success;
    private String message;
    private String token;
    private String refreshToken;
    private Long tokenExpiredAt; // timestamp millis
    private UserSummary user; 
}
