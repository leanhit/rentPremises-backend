package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor  // Đảm bảo có constructor với các tham số
public class UserSummary {
    private Long id;
    private String username;
    private String email;
    private String avatar;
}
