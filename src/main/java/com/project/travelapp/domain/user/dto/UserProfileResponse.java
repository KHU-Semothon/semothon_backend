package com.project.travelapp.domain.user.dto;

import com.project.travelapp.domain.user.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserProfileResponse {
    private Long userId;
    private String nickname;
    private Integer trustScore;
    private List<TravelExperienceDto> travelExperiences;

    public UserProfileResponse(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.trustScore = user.getTrustScore();
        this.travelExperiences = user.getTravelExperiences().stream()
                .map(TravelExperienceDto::new)
                .collect(Collectors.toList());
    }
}