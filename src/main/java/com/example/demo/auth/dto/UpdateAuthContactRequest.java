package com.example.demo.auth.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;

@Data
@Builder
public class UpdateAuthContactRequest {
    private String phone;
    private String address;
}
