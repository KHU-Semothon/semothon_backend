package com.project.travelapp.domain.pin.dto;

import com.project.travelapp.domain.pin.entity.Pin;
import lombok.Getter;

@Getter
public class PinResponse {
    private Long pinId;
    private Double latitude;
    private Double longitude;
    private String pinType;
    private String title;
    private Integer radius;

    public PinResponse(Pin pin) {
        this.pinId = pin.getId();
        this.latitude = pin.getLatitude();
        this.longitude = pin.getLongitude();
        this.pinType = pin.getPinType();
        this.title = pin.getTitle();
        this.radius = pin.getRadius();
    }
}