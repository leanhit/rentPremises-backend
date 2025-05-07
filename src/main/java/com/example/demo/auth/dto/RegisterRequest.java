package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Gender;
import com.example.demo.auth.entity.SystemRole;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    // Thông tin đăng nhập
    private String username;
    private String password;

    // Thông tin cá nhân
    private String fullName;
    private String dateOfBirth;
    private Gender gender;
    private String address;

    // Thông tin liên hệ
    private String email;
    private String phone;

    // Các role
    private SystemRole systemRole;

    private String avatar; // Đường dẫn hoặc tên file của ảnh đại diện người dùng
}
