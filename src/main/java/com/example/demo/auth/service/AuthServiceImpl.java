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


    public boolean checkUsernameExists(String username) {
        return authRepository.existsByUsername(username);
    }

    public boolean checkEmailExists(String email) {
        return authRepository.existsByEmail(email);
    }

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

        // Tạo đối tượng Auth
        Auth auth = Auth.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(request.getRole())
                .avatar(request.getAvatar())
                .build();

        // Tạo AuthInfo
        AuthInfo authInfo = AuthInfo.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE))
                .auth(auth)
                .build();

        // Tạo AuthContact với các trường mới: province, district, ward, street
        AuthContact authContact = AuthContact.builder()
                .phone(request.getPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getDetail()) // ánh xạ detail -> street
                .auth(auth)
                .build();

        // Gán Info và Contact cho Auth
        auth.setInfo(authInfo);
        auth.setContact(authContact);

        // Lưu vào DB
        authRepository.save(auth);

        // Tạo token
        String token = jwtUtils.generateToken(auth.getUsername());

        // Trả về phản hồi
        UserSummary summary = new UserSummary(
                auth.getId(),
                auth.getUsername(),
                auth.getEmail(),
                auth.getAvatar()
        );

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
        UserSummary summary = new UserSummary(
                auth.getId(), 
                auth.getUsername(), 
                auth.getEmail(), 
                auth.getAvatar(),  // Thêm avatar vào
                auth.getInfo() != null ? auth.getInfo().getFullName() : null, // Lấy tên đầy đủ từ AuthInfo
                auth.getInfo() != null ? auth.getInfo().getDateOfBirth() : null, // Lấy ngày sinh từ AuthInfo
                auth.getInfo() != null ? auth.getInfo().getGender().name() : null, // Giới tính từ AuthInfo
                auth.getContact() != null ? auth.getContact().getPhone() : null, // Số điện thoại từ AuthContact
                auth.getContact() != null ? auth.getContact().getProvince() : null, // Tỉnh từ AuthContact
                auth.getContact() != null ? auth.getContact().getDistrict() : null, // Quận/Huyện từ AuthContact
                auth.getContact() != null ? auth.getContact().getWard() : null, // Phường/Xã từ AuthContact
                auth.getContact() != null ? auth.getContact().getStreet() : null // Địa chỉ chi tiết từ AuthContact
        );

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

        // Avatar
        if (req.getAvatar() != null && !req.getAvatar().equals(auth.getAvatar())) {
            auth.setAvatar(req.getAvatar());
        }

        // AuthInfo
        if (auth.getInfo() != null) {
            if (req.getFullName() != null && !req.getFullName().equals(auth.getInfo().getFullName())) {
                auth.getInfo().setFullName(req.getFullName());
            }

            if (req.getBirthday() != null && !req.getBirthday().equals(auth.getInfo().getDateOfBirth())) {
                auth.getInfo().setDateOfBirth(req.getBirthday());
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
        // Chuyển đổi từ đối tượng Auth sang đối tượng UserDto
        return new UserSummary(auth.getId(), auth.getUsername(), auth.getEmail(), auth.getAvatar());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Trả về CustomUserDetails thay vì Auth
        return new CustomUserDetails(auth);
    }

}
