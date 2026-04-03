package com.project.travelapp.domain.media.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MediaService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public List<String> upload(List<MultipartFile> files) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "file");
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            uploadedUrls.add(baseUrl + "/uploads/" + filename);
        }
        return uploadedUrls;
    }
}
