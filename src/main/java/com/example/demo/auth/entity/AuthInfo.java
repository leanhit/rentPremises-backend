package com.example.demo.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.example.demo.auth.entity.Gender;
import com.example.demo.auth.entity.Auth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_auth_info")
public class AuthInfo {

    @Id
    private Long id; // Trùng với Auth.id

    @Column(nullable = true)
    private String fullName;

    @Column(nullable = true)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;
}
