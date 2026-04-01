package com.project.travelapp.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "travel_experiences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private Integer visitCount;

    @Column(nullable = false)
    private Integer stayMonths;

    @Builder
    public TravelExperience(User user, String country, Integer visitCount, Integer stayMonths) {
        this.user = user;
        this.country = country;
        this.visitCount = visitCount;
        this.stayMonths = stayMonths;
    }
}