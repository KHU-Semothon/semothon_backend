package com.project.travelapp.domain.answer.controller;

import com.project.travelapp.domain.answer.dto.AnswerCreateRequest;
import com.project.travelapp.domain.answer.service.AnswerService;
import com.project.travelapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1") // 경로를 최상위로 올립니다.
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // 답변 작성: /api/v1/questions/{questionId}/answers
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long answerId = answerService.createAnswer(questionId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "답변이 등록되었습니다.", Map.of("answerId", answerId)));
    }

    // 답변 채택: /api/v1/answers/{answerId}/accept
    @PostMapping("/answers/{answerId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptAnswer(
            @PathVariable Long answerId,
            @AuthenticationPrincipal UserDetails userDetails) {
        answerService.acceptAnswer(answerId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "답변이 채택되었습니다.", null));
    }
}