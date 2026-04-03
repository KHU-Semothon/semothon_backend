package com.project.travelapp.domain.answer.entity;

import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isAccepted = false;

    @ElementCollection
    @CollectionTable(name = "answer_media_urls", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls = new ArrayList<>();

    @Builder
    public Answer(Question question, User user, String content, List<String> mediaUrls) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.isAccepted = false;
        if (mediaUrls != null) {
            this.mediaUrls = mediaUrls;
        }
    }

    public void accept() {
        this.isAccepted = true;
    }
}