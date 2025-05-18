package com.example.demo.auth.repository;

import com.example.demo.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUsername(String username);

    Optional<Auth> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // ✅ Truy vấn đầy đủ: lấy cả info và contact
    @Query("SELECT a FROM Auth a " +
           "LEFT JOIN FETCH a.info " +
           "LEFT JOIN FETCH a.contact " +
           "WHERE a.username = :username")
    Optional<Auth> findByUsernameWithDetails(@Param("username") String username);
}
