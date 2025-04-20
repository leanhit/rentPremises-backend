package com.example.demo.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/**").permitAll()
                ).csrf(csrf -> csrf
                .ignoringRequestMatchers("/**") );


        return http.build();
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .authorizeHttpRequests(authorize -> authorize
    //             .requestMatchers("/api/auth/**").permitAll()  // Cho phép các API authentication (login/register)
    //             .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ ADMIN truy cập
    //             .anyRequest().authenticated() // Các API khác phải đăng nhập
    //         )
    //         .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/**")) // Chỉ tắt CSRF cho API Auth
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT không dùng session

    //     return http.build();
    // }
}
