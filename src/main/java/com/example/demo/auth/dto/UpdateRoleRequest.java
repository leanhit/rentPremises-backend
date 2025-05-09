package com.example.demo.auth.dto;

import com.example.demo.auth.entity.Role;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    private Role Role;
}
