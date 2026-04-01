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
    private LocalDateTime createdAt;

    public QuestionListResponse(Question question) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.category = question.getCategory();
        this.authorNickname = question.getUser().getNickname();
        this.answerCount = question.getAnswers().size();
        this.createdAt = question.getCreatedAt();
    }
}