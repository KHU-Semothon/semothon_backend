package com.project.travelapp.domain.question.dto;

import com.project.travelapp.domain.question.entity.Question;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuestionListResponse {
    private Long questionId;
    private String title;
    private String category;
    private String authorNickname;
    private int answerCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private Double latitude;
    private Double longitude;

    public QuestionListResponse(Question question) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.category = question.getCategory();
        this.authorNickname = question.getUser().getNickname();
        this.answerCount = question.getAnswers().size();
        this.likeCount = question.getLikeCount();
        this.createdAt = question.getCreatedAt();
        this.thumbnailUrl = question.getMediaUrls().isEmpty() ? null : question.getMediaUrls().get(0);
        this.latitude = question.getLatitude();
        this.longitude = question.getLongitude();
    }
}