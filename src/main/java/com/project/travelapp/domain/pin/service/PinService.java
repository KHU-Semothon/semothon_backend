package com.project.travelapp.domain.pin.service;

import com.project.travelapp.domain.pin.dto.PinCreateRequest;
import com.project.travelapp.domain.pin.dto.PinResponse;
import com.project.travelapp.domain.pin.entity.Pin;
import com.project.travelapp.domain.pin.repository.PinRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PinService {

    private final PinRepository pinRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPin(PinCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pin pin = Pin.builder()
                .user(user)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pinType(request.getPinType())
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        pinRepository.save(pin);
        return pin.getId();
    }

    @Transactional(readOnly = true)
    public List<PinResponse> getPinsInBoundingBox(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        // 지정된 위도/경도 범위 안의 핀들을 조회하여 DTO 리스트로 변환
        return pinRepository.findByLatitudeBetweenAndLongitudeBetween(minLat, maxLat, minLng, maxLng)
                .stream()
                .map(PinResponse::new)
                .collect(Collectors.toList());
    }
}