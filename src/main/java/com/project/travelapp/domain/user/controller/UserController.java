package com.project.travelapp.domain.user.controller;

import com.project.travelapp.domain.user.dto.UserProfileResponse;
import com.project.travelapp.domain.user.service.UserService;
import com.project.travelapp.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse response = userService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(200, "프로필 조회 성공", response));
    }
}