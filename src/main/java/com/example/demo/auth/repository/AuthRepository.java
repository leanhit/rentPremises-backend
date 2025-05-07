package com.example.demo.auth.repository;

import com.example.demo.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // << thêm dòng này
import org.springframework.data.repository.query.Param; // << và dòng này

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUsername(String username);

    Optional<Auth> findByEmail(String email);
    
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
