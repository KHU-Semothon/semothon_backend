package com.project.travelapp.domain.pin.controller;

import com.project.travelapp.domain.pin.dto.PinCreateRequest;
import com.project.travelapp.domain.pin.dto.PinCreateResponse;
import com.project.travelapp.domain.pin.dto.PinResponse;
import com.project.travelapp.domain.pin.service.PinService;
import com.project.travelapp.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pins")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @PostMapping
    public ResponseEntity<ApiResponse<PinCreateResponse>> createPin(
            @Valid @RequestBody PinCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long pinId = pinService.createPin(request, userDetails.getUsername());

        // 명세서와 동일한 메시지 및 JSON 구조로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "지도 정보가 등록되었습니다.", new PinCreateResponse(pinId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PinResponse>>> getPinsInBoundingBox(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        List<PinResponse> response = pinService.getPinsInBoundingBox(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(ApiResponse.success(200, "핀 목록 조회 성공", response));
    }
}