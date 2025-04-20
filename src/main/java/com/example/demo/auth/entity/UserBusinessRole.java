package com.example.demo.auth.entity;

import com.example.demo.auth.entity.Auth;
import com.example.demo.auth.entity.BusinessRole;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user_business_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBusinessRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Một User có nhiều BusinessRole
    @JoinColumn(name = "user_id")
    private Auth user;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_role")
    private BusinessRole businessRole;
}
