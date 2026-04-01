package com.project.travelapp.domain.user.controller;

import com.project.travelapp.domain.user.dto.LoginRequest;
import com.project.travelapp.domain.user.dto.LoginResponse;
import com.project.travelapp.domain.user.dto.SignupRequest;
import com.project.travelapp.domain.user.dto.SignupResponse;
import com.project.travelapp.domain.user.service.AuthService;
import com.project.travelapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입이 완료되었습니다.", new SignupResponse(userId)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(200, "로그인 성공", response));
    }
}