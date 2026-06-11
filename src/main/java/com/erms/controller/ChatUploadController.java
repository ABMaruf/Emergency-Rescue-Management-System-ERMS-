package com.erms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/webp",
            "image/gif",
            "image/bmp",
            "image/avif",
            "image/heic",
            "image/heif"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp",
            ".gif",
            ".bmp",
            ".avif",
            ".jfif",
            ".heic",
            ".heif"
    );

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "File must be less than 5MB"));
            }

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalName.substring(dotIndex).toLowerCase();
            }

            String contentType = file.getContentType();
            boolean allowedType = contentType != null
                    && (contentType.toLowerCase().startsWith("image/")
                    || ALLOWED_TYPES.contains(contentType.toLowerCase()));
            boolean allowedExtension = ALLOWED_EXTENSIONS.contains(extension);
            boolean cameraBlobAllowed = contentType != null
                    && contentType.equalsIgnoreCase("application/octet-stream")
                    && extension.isBlank();
            if (!allowedType && !allowedExtension && !cameraBlobAllowed) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            if (extension.isBlank()) {
                extension = extensionFromContentType(contentType);
            }

            String fileName = UUID.randomUUID() + extension;
            Path savedPath = uploadPath.resolve(fileName).normalize();
            if (!savedPath.startsWith(uploadPath)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file path"));
            }

            Files.copy(file.getInputStream(), savedPath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of(
                    "message", "Upload successful",
                    "imageUrl", "/uploads/chat-images/" + fileName
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload image"));
        }
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }

        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "image/bmp" -> ".bmp";
            case "image/avif" -> ".avif";
            case "image/heic" -> ".heic";
            case "image/heif" -> ".heif";
            default -> ".jpg";
        };
    }
}
