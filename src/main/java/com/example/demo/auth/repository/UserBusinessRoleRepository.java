package com.example.demo.auth.repository;

import com.example.demo.auth.entity.UserBusinessRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBusinessRoleRepository extends JpaRepository<UserBusinessRole, Long> {
    List<UserBusinessRole> findByUserId(Long userId);
}
