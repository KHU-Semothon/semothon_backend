package com.project.travelapp.domain.question.controller;

import com.project.travelapp.domain.question.dto.LikeResponse;
import com.project.travelapp.domain.question.dto.QuestionCreateRequest;
import com.project.travelapp.domain.question.dto.QuestionDetailResponse;
import com.project.travelapp.domain.question.dto.QuestionListResponse;
import com.project.travelapp.domain.question.service.QuestionService;
import com.project.travelapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long questionId = questionService.createQuestion(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "질문이 등록되었습니다.", Map.of("questionId", questionId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionListResponse>>> getQuestions(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<QuestionListResponse> response = questionService.getQuestions(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(200, "질문 목록 조회 성공", response));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = (userDetails != null) ? userDetails.getUsername() : null;
        QuestionDetailResponse response = questionService.getQuestionDetail(questionId, email);
        return ResponseEntity.ok(ApiResponse.success(200, "질문 상세 조회 성공", response));
    }

    @PostMapping("/{questionId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        LikeResponse response = questionService.toggleLike(questionId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "좋아요 상태가 변경되었습니다.", response));
    }
}
