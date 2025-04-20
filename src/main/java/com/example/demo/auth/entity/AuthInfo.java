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

    private String fullName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;
}
