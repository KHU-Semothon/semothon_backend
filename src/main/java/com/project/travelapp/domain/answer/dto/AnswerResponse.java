package com.project.travelapp.domain.answer.dto;

import com.project.travelapp.domain.answer.entity.Answer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnswerResponse {
    private Long answerId;
    private String content;
    private String authorNickname;
    private Integer authorTrustScore; // 추가됨!
    private Boolean isAccepted;
    private LocalDateTime createdAt;

    public AnswerResponse(Answer answer) {
        this.answerId = answer.getId();
        this.content = answer.getContent();
        this.authorNickname = answer.getUser().getNickname();
        this.authorTrustScore = answer.getUser().getTrustScore(); // 추가됨!
        this.isAccepted = answer.getIsAccepted();
        this.createdAt = answer.getCreatedAt();
    }
}