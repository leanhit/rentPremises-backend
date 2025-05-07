package com.example.demo.auth.repository;

import com.example.demo.auth.entity.AuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthInfoRepository extends JpaRepository<AuthInfo, Long> {
}