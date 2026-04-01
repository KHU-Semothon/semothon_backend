package com.project.travelapp.domain.question.controller;

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

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long questionId = questionService.createQuestion(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "질문이 등록되었습니다.", questionId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionListResponse>>> getQuestions(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<QuestionListResponse> response = questionService.getQuestions(pageable);
        return ResponseEntity.ok(ApiResponse.success(200, "질문 목록 조회 성공", response));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionDetailResponse>> getQuestionDetail(@PathVariable Long questionId) {
        QuestionDetailResponse response = questionService.getQuestionDetail(questionId);
        return ResponseEntity.ok(ApiResponse.success(200, "질문 상세 조회 성공", response));
    }
}