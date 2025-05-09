package com.example.demo.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String address;
    private String avatar; // Link ảnh đại diện mới
}
