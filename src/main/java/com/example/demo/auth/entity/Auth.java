package com.example.demo.auth.entity;

import com.example.demo.auth.entity.Role;
import com.example.demo.auth.entity.AuthContact;
import com.example.demo.auth.entity.AuthInfo;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_auth")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Role role;

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthInfo info;

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthContact contact;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String avatar;  // Lưu URL hoặc tên file ảnh đại diện của người dùng
}
