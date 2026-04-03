package com.project.travelapp.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponse {
    private Boolean isLiked;
    private int likeCount;
}
