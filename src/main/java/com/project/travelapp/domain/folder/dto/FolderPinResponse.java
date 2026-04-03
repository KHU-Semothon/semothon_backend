package com.project.travelapp.domain.folder.dto;

import com.project.travelapp.domain.question.entity.Question;
import lombok.Getter;

@Getter
public class FolderPinResponse {
    private Long questionId;
    private Double latitude;
    private Double longitude;
    private String category;
    private String title;
    private String thumbnailUrl;

    public FolderPinResponse(Question question) {
        this.questionId = question.getId();
        this.latitude = question.getLatitude();
        this.longitude = question.getLongitude();
        this.category = question.getCategory();
        this.title = question.getTitle();
        this.thumbnailUrl = question.getMediaUrls().isEmpty() ? null : question.getMediaUrls().get(0);
    }
}