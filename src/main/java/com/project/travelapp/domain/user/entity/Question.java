package com.project.travelapp.domain.question.entity;

import com.project.travelapp.domain.answer.entity.Answer;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(length = 100)
    private String locationKeyword;

    @Column(nullable = false)
    private int likeCount = 0;

    @ElementCollection
    @CollectionTable(name = "question_media_urls", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    private Double latitude;
    private Double longitude;

    @Builder
    public Question(User user, String title, String content, String category, String locationKeyword, Double latitude, Double longitude, List<String> mediaUrls) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.category = category;
        this.locationKeyword = locationKeyword;
        this.likeCount = 0;
        if (mediaUrls != null) {
            this.mediaUrls = mediaUrls;
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}