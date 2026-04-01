package com.project.travelapp.domain.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class QuestionCreateRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "카테고리를 선택해주세요.")
    private String category;

    private String locationKeyword; // 선택 사항
}