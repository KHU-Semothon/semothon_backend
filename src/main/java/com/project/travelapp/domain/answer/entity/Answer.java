package com.project.travelapp.domain.answer.entity;

import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Builder
    public Answer(Question question, User user, String content) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.isAccepted = false;
    }

    public void accept() {
        this.isAccepted = true;
    }
}