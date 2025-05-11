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
    private Long id; // Khóa chính trùng với Auth.id

    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    private String province;

    @Column(nullable = true)
    private String district;

    @Column(nullable = true)
    private String ward;

    @Column(nullable = true)
    private String street; // hoặc addressDetail

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;
}
