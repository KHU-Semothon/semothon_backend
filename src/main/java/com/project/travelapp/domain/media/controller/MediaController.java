package com.project.travelapp.domain.media.controller;

import com.project.travelapp.domain.media.service.MediaService;
import com.project.travelapp.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> upload(
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = mediaService.upload(files);
        return ResponseEntity.ok(ApiResponse.success(200, "미디어 업로드 성공",
                Map.of("uploadedUrls", uploadedUrls)));
    }
}
