package com.project.travelapp.domain.pin.entity;

import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 30)
    private String pinType; // DANGER, RESTAURANT, CAUTION

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public Pin(User user, Double latitude, Double longitude, String pinType, String title, String description) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pinType = pinType;
        this.title = title;
        this.description = description;
    }
}