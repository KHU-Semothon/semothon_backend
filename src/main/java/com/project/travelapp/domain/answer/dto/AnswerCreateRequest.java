package com.project.travelapp.domain.answer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class AnswerCreateRequest {
    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String content;

    private List<String> mediaUrls;
}