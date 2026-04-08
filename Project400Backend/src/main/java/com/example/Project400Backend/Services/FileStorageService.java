package com.example.Project400Backend.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public StoredFile store(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename());
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot >= 0) {
            extension = originalFilename.substring(lastDot);
        }

        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path targetDir = uploadRoot.resolve(folderName).normalize();
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedFilename).normalize();

            if (!targetFile.startsWith(uploadRoot)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return new StoredFile(
                    originalFilename,
                    storedFilename,
                    targetFile.toString(),
                    file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }
    }

    public record StoredFile(
            String originalFilename,
            String storedFilename,
            String storagePath,
            String contentType,
            long fileSize
    ) {
    }
}
