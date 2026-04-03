package com.project.travelapp.domain.question.dto;

import com.project.travelapp.domain.answer.dto.AnswerResponse;
import com.project.travelapp.domain.question.entity.Question;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QuestionDetailResponse {
    private Long questionId;
    private String title;
    private String content;
    private String category;
    private String authorNickname;
    private int likeCount;
    private boolean isLiked;
    private List<String> mediaUrls;

    // 💡 Entity인 Answer 대신 응답용 DTO 리스트로 변경!
    private List<AnswerResponse> answers;

    public QuestionDetailResponse(Question question, boolean isLiked) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.category = question.getCategory();
        this.authorNickname = question.getUser().getNickname();
        this.likeCount = question.getLikeCount();
        this.isLiked = isLiked;

        // 컬렉션(List) 데이터는 반드시 Transaction 안에서 값을 미리 꺼내놔야 합니다.
        this.mediaUrls = (question.getMediaUrls() != null)
                ? new ArrayList<>(question.getMediaUrls())
                : new ArrayList<>();

        // Answer 엔티티 리스트를 AnswerResponse DTO 리스트로 싹 변환!
        this.answers = question.getAnswers().stream()
                .map(AnswerResponse::new)
                .collect(Collectors.toList());
    }
}