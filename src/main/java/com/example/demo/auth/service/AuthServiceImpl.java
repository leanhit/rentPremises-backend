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
        // Check username tồn tại
        if (authRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Check email tồn tại
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Tạo đối tượng Auth với các thông tin từ request, bao gồm avatar
        Auth auth = Auth.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(request.getRole())
                .avatar(request.getAvatar())  // Thêm avatar
                .build();

        // Tạo AuthInfo với thông tin từ request
        AuthInfo authInfo = AuthInfo.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE))
                .auth(auth)
                .build();

        // Tạo AuthContact với thông tin từ request
        AuthContact authContact = AuthContact.builder()
                .phone(request.getPhone())
                .address(request.getAddress())
                .auth(auth)
                .build();

        // Set AuthInfo và AuthContact vào Auth
        auth.setInfo(authInfo);
        auth.setContact(authContact);

        // Lưu đối tượng Auth vào cơ sở dữ liệu
        authRepository.save(auth);

        // Tạo JWT token cho người dùng mới
        String token = jwtUtils.generateToken(auth.getUsername());

        // Tạo đối tượng UserSummary với thông tin người dùng đã đăng ký
        UserSummary summary = new UserSummary(
                auth.getId(),
                auth.getUsername(),
                auth.getEmail(),
                auth.getAvatar()  // Thêm avatar vào UserSummary
        );

        // Trả về thông tin đăng ký, bao gồm token, userSummary và các thông tin cần thiết
        return RegisterResponse.builder()
                .success(true)
                .message("Register successful")
                .token(token)
                .refreshToken(null) // Nếu bạn làm refreshToken thì thêm vào
                .tokenExpiredAt(System.currentTimeMillis() + 3600 * 1000) // 1h
                .user(summary)  // Thêm đối tượng user vào response
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
        String refreshToken = null; // Nếu bạn làm refreshToken thì thêm vào

        // Tạo đối tượng UserSummary với các tham số từ Auth
        UserSummary summary = new UserSummary(
                auth.getId(), 
                auth.getUsername(), 
                auth.getEmail(), 
                auth.getAvatar() // Thêm avatar vào
        );

        // Trả về LoginResponse
        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .refreshToken(refreshToken)
                .Role(auth.getRole())
                .user(summary)  // Thêm đối tượng user vào response
                .build();
    }

    public void updateAuthInfo(Long authId, UpdateAuthInfoRequest request) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        AuthInfo info = auth.getInfo();
        if (info == null) {
            info = new AuthInfo();
            info.setAuth(auth);
            info.setId(auth.getId());
        }

        info.setFullName(request.getFullName());
        info.setDateOfBirth(request.getDateOfBirth());
        info.setGender(request.getGender());

        authInfoRepository.save(info);
    }

    public void updateAuthContact(Long authId, UpdateAuthContactRequest request) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        AuthContact contact = auth.getContact();
        if (contact == null) {
            contact = new AuthContact();
            contact.setAuth(auth);
            contact.setId(auth.getId());
        }

        contact.setPhone(request.getPhone());
        contact.setAddress(request.getAddress());

        authContactRepository.save(contact);
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
    public void changeRole(Long authId, UpdateRoleRequest request) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException("Auth not found"));

        auth.setRole(request.getRole());
        authRepository.save(auth);
    }

    @Override
    @Transactional
    public void updateProfile(Long id, ProfileUpdateRequest req) {
        Auth auth = authRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getAvatar() != null) {
            auth.setAvatar(req.getAvatar());
        }
        if (auth.getInfo() != null && req.getFullName() != null) {
            auth.getInfo().setFullName(req.getFullName());
        }
        if (auth.getContact() != null) {
            if (req.getPhone() != null) auth.getContact().setPhone(req.getPhone());
            if (req.getAddress() != null) auth.getContact().setAddress(req.getAddress());
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
