package com.project.travelapp.domain.pin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PinCreateRequest {
    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotBlank(message = "핀 타입을 지정해주세요 (ex: DANGER, RESTAURANT)")
    private String pinType;

    @NotBlank(message = "핀 제목을 입력해주세요.")
    private String title;

    private String description;
}