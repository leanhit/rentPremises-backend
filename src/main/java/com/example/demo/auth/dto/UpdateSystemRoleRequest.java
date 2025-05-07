package com.example.demo.auth.dto;

import com.example.demo.auth.entity.SystemRole;
import lombok.Data;

@Data
public class UpdateSystemRoleRequest {
    private SystemRole systemRole;
}
