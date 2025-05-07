package com.example.demo.auth.dto;

import lombok.Data;
import lombok.Builder;
import com.example.demo.auth.entity.SystemRole;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;        // access token
    private String refreshToken; // optional nếu bạn có implement refresh
    private SystemRole systemRole;
    private UserSummary user; 
}
