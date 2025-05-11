package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Gender;
import com.example.demo.auth.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    // Thông tin đăng nhập
    private String username;
    private String password;

    // Thông tin cá nhân
    private String fullName;
    private String dateOfBirth; // Có thể là String hoặc LocalDate nếu cần format
    private Gender gender; // Kiểu enum cho Gender
    private String province; 
    private String district; 
    private String ward; 
    private String detail; 

    // Thông tin liên hệ
    private String email;
    private String phone;

    // Các role
    private Role role; // Role với kiểu enum

    private String avatar; // Đường dẫn hoặc tên file của ảnh đại diện người dùng
}
