package com.project.travelapp.domain.answer.dto;

import com.project.travelapp.domain.answer.entity.Answer;
import com.project.travelapp.domain.user.entity.TravelExperience;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
public class AnswerResponse {
    private Long answerId;
    private String content;
    private String authorNickname;
    private Integer authorTrustScore;
    private String authorExperience;
    private Boolean isAccepted;
    private LocalDateTime createdAt;
    private List<String> mediaUrls;

    public AnswerResponse(Answer answer) {
        this.answerId = answer.getId();
        this.content = answer.getContent();
        this.authorNickname = answer.getUser().getNickname();
        this.authorTrustScore = answer.getUser().getTrustScore();
        this.authorExperience = buildExperience(answer);
        this.isAccepted = answer.getIsAccepted();
        this.createdAt = answer.getCreatedAt();
        this.mediaUrls = (answer.getMediaUrls() != null)
                ? new ArrayList<>(answer.getMediaUrls())
                : new ArrayList<>();
    }

    private String buildExperience(Answer answer) {
        List<TravelExperience> experiences = answer.getUser().getTravelExperiences();
        if (experiences == null || experiences.isEmpty()) {
            return null;
        }
        TravelExperience exp = experiences.get(0);
        return exp.getCountry() + " " + exp.getVisitCount() + "회 방문";
    }
}