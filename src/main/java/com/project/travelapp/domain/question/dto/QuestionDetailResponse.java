package com.project.travelapp.domain.question.dto;

import com.project.travelapp.domain.answer.dto.AnswerResponse;
import com.project.travelapp.domain.question.entity.Question;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QuestionDetailResponse {
    private Long questionId;
    private String title;
    private String content;
    private String category;
    private String locationKeyword;
    private String authorNickname;
    private LocalDateTime createdAt;
    private int likeCount;
    private Boolean isLiked;
    private List<String> mediaUrls;
    private List<AnswerResponse> answers;

    public QuestionDetailResponse(Question question, boolean isLiked) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.category = question.getCategory();
        this.locationKeyword = question.getLocationKeyword();
        this.authorNickname = question.getUser().getNickname();
        this.createdAt = question.getCreatedAt();
        this.likeCount = question.getLikeCount();
        this.isLiked = isLiked;
        this.mediaUrls = question.getMediaUrls();
        this.answers = question.getAnswers().stream()
                .map(AnswerResponse::new)
                .collect(Collectors.toList());
    }
}