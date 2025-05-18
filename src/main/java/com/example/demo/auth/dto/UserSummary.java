package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String email;
    private String avatar;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;

    private String phone;
    private String province; 
    private String district; 
    private String ward; 
    private String detail; 

    // ✅ Constructor rút gọn (chỉ dùng 4 trường chính)
    public UserSummary(Long id, String username, String email, String avatar) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;

        this.fullName = null;
        this.dateOfBirth = null;
        this.gender = null;
        this.phone = null;
        this.province = null;
        this.district = null;
        this.ward = null;
        this.detail = null;
    }    
}
