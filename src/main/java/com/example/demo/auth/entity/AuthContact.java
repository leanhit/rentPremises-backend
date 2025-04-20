package com.example.demo.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.example.demo.auth.entity.Auth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_auth_contact")
public class AuthContact {
    @Id
    private Long id; // Khóa chính trùng với AuthLogin.id

    private String phone;
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;
}
