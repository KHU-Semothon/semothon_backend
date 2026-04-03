package com.project.travelapp.domain.folder.controller;

import com.project.travelapp.domain.folder.dto.FolderCreateRequest;
import com.project.travelapp.domain.folder.dto.FolderPinResponse;
import com.project.travelapp.domain.folder.dto.FolderResponse;
import com.project.travelapp.domain.folder.service.FolderService;
import com.project.travelapp.domain.question.dto.QuestionListResponse;
import com.project.travelapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    // 4-1. 폴더 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createFolder(
            @Valid @RequestBody FolderCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long folderId = folderService.createFolder(request.getName(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "폴더가 생성되었습니다.", Map.of("folderId", folderId)));
    }

    // 4-2. 내 폴더 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getFolders(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FolderResponse> response = folderService.getFolders(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "폴더 목록 조회 성공", response));
    }

    // 4-3. 폴더에 게시물 저장 (스크랩)
    @PostMapping("/{folderId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> addQuestionToFolder(
            @PathVariable Long folderId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        folderService.addQuestionToFolder(folderId, questionId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "폴더에 게시물이 저장되었습니다.", null));
    }

    // 4-4. 폴더 내 화면 영역 핀 목록 조회
    @GetMapping("/{folderId}/pins")
    public ResponseEntity<ApiResponse<List<FolderPinResponse>>> getFolderPinsWithinBounds(
            @PathVariable Long folderId,
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FolderPinResponse> response = folderService.getFolderPinsWithinBounds(
                folderId, minLat, maxLat, minLng, maxLng, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "폴더 내 핀 목록 조회 성공", response));
    }

    @GetMapping("/{folderId}/questions")
    public ResponseEntity<ApiResponse<List<QuestionListResponse>>> getQuestionsInFolder(
            @PathVariable Long folderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<QuestionListResponse> response = folderService.getQuestionsInFolder(
                folderId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success(200, "폴더 내 게시물 목록 조회 성공", response));
    }
}