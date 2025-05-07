package com.example.demo.auth.repository;

import com.example.demo.auth.entity.AuthContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthContactRepository extends JpaRepository<AuthContact, Long> {
}