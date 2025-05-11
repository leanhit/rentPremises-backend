package com.example.demo.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    // Từ Auth
    private String username;
    private String email;
    private String avatar;

    // Từ AuthInfo
    private String fullName;
    private LocalDate birthday;
    private String gender;

    // Từ AuthContact
    private String phone;    
    private String province;
    private String district;
    private String ward;
    private String detail;
}

