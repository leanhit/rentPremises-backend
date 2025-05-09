package com.example.demo.auth.security;

import com.example.demo.auth.entity.Auth;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Auth auth;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về quyền từ Auth nếu có, ví dụ: Collections.singleton(new SimpleGrantedAuthority(auth.getRole()))
        return Collections.emptyList(); // hoặc return quyền nếu có
    }

    @Override
    public String getPassword() {
        return auth.getPassword(); // lấy từ entity Auth
    }

    @Override
    public String getUsername() {
        return auth.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Auth getAuth() {
        return auth; // bạn có thể dùng để lấy thông tin đầy đủ trong controller nếu cần
    }
}
