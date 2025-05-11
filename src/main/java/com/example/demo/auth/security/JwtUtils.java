package com.example.demo.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Base64;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKeyBase64; // Lấy từ application.properties

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // Lấy từ application.properties

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Phương thức tạo Refresh Token
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationTime); // Thêm thời gian hết hạn

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKeyBase64)
                .compact();
    }

    // Phương thức để kiểm tra xem refresh token có hợp lệ hay không
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKeyBase64).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}