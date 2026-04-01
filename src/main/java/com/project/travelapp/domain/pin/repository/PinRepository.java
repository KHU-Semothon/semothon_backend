package com.project.travelapp.domain.pin.repository;

import com.project.travelapp.domain.pin.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Long> {
    // 지도 화면의 남서쪽(min), 북동쪽(max) 좌표를 받아 그 안의 핀만 검색!
    List<Pin> findByLatitudeBetweenAndLongitudeBetween(Double minLat, Double maxLat, Double minLng, Double maxLng);
}