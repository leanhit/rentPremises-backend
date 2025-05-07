package com.example.demo.auth.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import com.example.demo.auth.entity.Gender;

@Data
@Builder
public class UpdateAuthInfoRequest {
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
}