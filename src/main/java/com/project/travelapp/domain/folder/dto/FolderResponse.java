package com.project.travelapp.domain.folder.dto;

import com.project.travelapp.domain.folder.entity.Folder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class FolderResponse {
    private Long folderId;
    private String name;
    private LocalDateTime createdAt;

    public FolderResponse(Folder folder) {
        this.folderId = folder.getId();
        this.name = folder.getName();
        this.createdAt = folder.getCreatedAt();
    }
}