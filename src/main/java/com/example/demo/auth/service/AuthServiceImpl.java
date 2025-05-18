package com.example.demo.auth.service.impl;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.*;
import com.example.demo.auth.repository.*;
import com.example.demo.auth.security.*;
import com.example.demo.auth.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final AuthInfoRepository authInfoRepository;
    private final AuthContactRepository authContactRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (authRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Kiểm tra email đã tồn tại chưa
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Bước 1: Tạo Auth (chưa có ID)
        Auth auth = Auth.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(request.getRole())
                .avatar(request.getAvatar())
                .build();

        // Bước 2: Tạo AuthInfo (gắn với auth)
        AuthInfo authInfo = AuthInfo.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE))
                .auth(auth)
                .build();

        // Bước 3: Tạo AuthContact (gắn với auth)
        AuthContact authContact = AuthContact.builder()
                .phone(request.getPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getDetail()) // ánh xạ detail -> street
                .auth(auth)
                .build();

        // Bước 4: Gán info và contact vào auth
        auth.setInfo(authInfo);
        auth.setContact(authContact);

        // Bước 5: Lưu auth - JPA sẽ cascade info và contact
        auth = authRepository.save(auth);

        // Bước 6: Tạo token
        String token = jwtUtils.generateToken(auth.getUsername());

        // Tạo thông tin tóm tắt trả về        
        UserSummary summary = toUserDto(auth);

        return RegisterResponse.builder()
                .success(true)
                .message("Register successful")
                .token(token)
                .refreshToken(null)
                .tokenExpiredAt(System.currentTimeMillis() + 3600 * 1000) // 1 giờ
                .user(summary)
                .build();
    }
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Tìm kiếm người dùng từ cơ sở dữ liệu
        Auth auth = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Kiểm tra trạng thái tài khoản và mật khẩu
        if (!auth.isActive() || !passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Tạo JWT token
        String token = jwtUtils.generateToken(auth.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(auth.getUsername());  // Đã thêm tạo refresh token

        // Tạo đối tượng UserSummary với các tham số từ Auth
        UserSummary summary = toUserDto(auth);

        // Trả về LoginResponse
        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .refreshToken(refreshToken)
                .role(auth.getRole()) // Trả về role
                .user(summary)  // Thêm đối tượng user vào response
                .build();
    }

    @Override
    public void changePassword(Long authId, ChangePasswordRequest request) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), auth.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        auth.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authRepository.save(auth);
    }

    @Override
    @Transactional
    public void updateProfile(Long id, ProfileUpdateRequest req) {
        Auth auth = authRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // AuthInfo
        if (auth.getInfo() != null) {
            if (req.getFullName() != null && !req.getFullName().equals(auth.getInfo().getFullName())) {
                auth.getInfo().setFullName(req.getFullName());
            }

            if (req.getDateOfBirth() != null && !req.getDateOfBirth().equals(auth.getInfo().getDateOfBirth())) {
                auth.getInfo().setDateOfBirth(req.getDateOfBirth());
            }

            if (req.getGender() != null) {
                try {
                    Gender newGender = Gender.valueOf(req.getGender().toUpperCase());
                    if (!newGender.equals(auth.getInfo().getGender())) {
                        auth.getInfo().setGender(newGender);
                    }
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid gender value: " + req.getGender());
                }
            }
        }

        // AuthContact
        if (auth.getContact() != null) {
            AuthContact contact = auth.getContact();

            if (req.getPhone() != null && !req.getPhone().equals(contact.getPhone())) {
                contact.setPhone(req.getPhone());
            }
            if (req.getProvince() != null && !req.getProvince().equals(contact.getProvince())) {
                contact.setProvince(req.getProvince());
            }
            if (req.getDistrict() != null && !req.getDistrict().equals(contact.getDistrict())) {
                contact.setDistrict(req.getDistrict());
            }
            if (req.getWard() != null && !req.getWard().equals(contact.getWard())) {
                contact.setWard(req.getWard());
            }
            if (req.getDetail() != null && !req.getDetail().equals(contact.getStreet())) {
                contact.setStreet(req.getDetail());
            }
        }

        authRepository.save(auth);
    }

    @Override
    public UserSummary toUserDto(Auth auth) {
        return new UserSummary(
            auth.getId(),
            auth.getUsername(),
            auth.getEmail(),
            auth.getAvatar(),
            auth.getInfo() != null ? auth.getInfo().getFullName() : null,
            auth.getInfo() != null ? auth.getInfo().getDateOfBirth() : null,
            auth.getInfo() != null && auth.getInfo().getGender() != null ? auth.getInfo().getGender().name() : null,
            auth.getContact() != null ? auth.getContact().getPhone() : null,
            auth.getContact() != null ? auth.getContact().getProvince() : null,
            auth.getContact() != null ? auth.getContact().getDistrict() : null,
            auth.getContact() != null ? auth.getContact().getWard() : null,
            auth.getContact() != null ? auth.getContact().getStreet() : null
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Trả về CustomUserDetails thay vì Auth
        return new CustomUserDetails(auth);
    }
}
