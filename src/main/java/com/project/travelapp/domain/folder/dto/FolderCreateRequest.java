package com.project.travelapp.domain.folder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FolderCreateRequest {
    @NotBlank(message = "폴더 이름을 입력해주세요.")
    private String name;
}