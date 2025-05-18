package com.example.demo.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
public class ImgurUpload {

    private static final Logger logger = LoggerFactory.getLogger(ImgurUpload.class);

    private final RestTemplate restTemplate;
    private final String clientId;

    public ImgurUpload(RestTemplate restTemplate, @Value("${imgur.clientId}") String clientId) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
    }

    public String uploadToImgur(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.error("❌ File tải lên bị null hoặc rỗng.");
            throw new IllegalArgumentException("File tải lên không được rỗng.");
        }

        logger.info("📤 Bắt đầu chuyển ảnh sang base64: {} (size={} bytes)", file.getOriginalFilename(), file.getSize());

        // Encode ảnh sang base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);
        body.add("type", "base64");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        logger.debug("🔄 Gửi yêu cầu POST đến Imgur API...");
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity("https://api.imgur.com/3/image", requestEntity, Map.class);
        } catch (Exception e) {
            logger.error("❌ Lỗi khi gọi API Imgur: {}", e.getMessage());
            throw new ImageUploadException("Không thể kết nối tới Imgur API.");
        }

        logger.debug("✅ Nhận phản hồi từ Imgur: {}", response.getStatusCode());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            logger.debug("📝 Dữ liệu phản hồi từ Imgur: {}", response.getBody());

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            if (data != null && data.containsKey("link")) {
                String imageUrl = (String) data.get("link");
                logger.info("🎉 Ảnh tải lên thành công! URL: {}", imageUrl);
                return imageUrl;
            } else {
                logger.error("⚠️ Phản hồi từ Imgur không chứa link ảnh.");
            }
        }

        logger.error("❌ Upload ảnh thất bại với mã phản hồi: {}", response.getStatusCode());
        throw new ImageUploadException("Upload ảnh thất bại: " + response.getStatusCode());
    }

    // Custom Exception Class
    public static class ImageUploadException extends RuntimeException {
        public ImageUploadException(String message) {
            super(message);
        }
    }
}
