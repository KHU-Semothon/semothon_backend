package com.project.travelapp.domain.folder.entity;

import com.project.travelapp.domain.question.entity.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "folder_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FolderQuestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Builder
    public FolderQuestion(Folder folder, Question question) {
        this.folder = folder;
        this.question = question;
    }
}