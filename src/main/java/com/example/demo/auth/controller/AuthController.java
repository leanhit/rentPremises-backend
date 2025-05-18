package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.Auth;
import com.example.demo.auth.service.AuthService;
//import com.example.demo.auth.utils.ImgurUpload;
import com.example.demo.auth.utils.ImgBBUpload;
import com.example.demo.auth.repository.AuthRepository;
import com.example.demo.auth.utils.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import com.example.demo.auth.repository.AuthRepository;
import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthContext authContext;
    private final AuthRepository userRepository;
    //private final ImgurUpload imgurUpload;
    private final ImgBBUpload imgbbUpload; // Sử dụng ImgBBUpload thay vì ImgurUpload
    @Autowired
    private AuthRepository authRepository;

    // Kiểm tra username tồn tại
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    // Kiểm tra email tồn tại
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("Received request: " + request); 
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{authId}/change-password")
    public void changePassword(@PathVariable Long authId, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authId, request);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Auth auth = authContext.getCurrentAuth(); 
        return ResponseEntity.ok(authService.toUserDto(auth));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest req) {
        Auth auth = authContext.getCurrentAuth(); 
        authService.updateProfile(auth.getId(), req);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatar(@RequestPart("avatar") MultipartFile avatarFile) {
        try {
            if (avatarFile == null || avatarFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Avatar file is required."));
            }

            Auth auth = authContext.getCurrentAuth();
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access."));
            }

            String uploadedUrl = imgbbUpload.upload(avatarFile); // sử dụng ImgBBUpload thay vì ImgurUpload

            auth.setAvatar(uploadedUrl);
            authRepository.save(auth);

            return ResponseEntity.ok(Map.of("avatar", uploadedUrl));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload avatar."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }


    // @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<?> updateAvatar(@RequestPart("avatar") MultipartFile avatarFile) {
    //     try {
    //         if (avatarFile == null || avatarFile.isEmpty()) {
    //             return ResponseEntity.badRequest().body(Map.of("error", "Avatar file is required."));
    //         }

    //         Auth auth = authContext.getCurrentAuth();
    //         if (auth == null) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access."));
    //         }

    //         String uploadedUrl = imgurUpload.uploadToImgur(avatarFile); // Sử dụng service bạn đã có

    //         auth.setAvatar(uploadedUrl);
    //         authRepository.save(auth);

    //         return ResponseEntity.ok(Map.of("avatar", uploadedUrl));
    //     } catch (IOException e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload avatar."));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
    //     }
    // }
}
