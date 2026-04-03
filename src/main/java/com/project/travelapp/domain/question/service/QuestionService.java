package com.project.travelapp.domain.question.service;

import com.project.travelapp.domain.question.dto.LikeResponse;
import com.project.travelapp.domain.question.dto.QuestionCreateRequest;
import com.project.travelapp.domain.question.dto.QuestionDetailResponse;
import com.project.travelapp.domain.question.dto.QuestionListResponse;
import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.entity.QuestionLike;
import com.project.travelapp.domain.question.repository.QuestionLikeRepository;
import com.project.travelapp.domain.question.repository.QuestionRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createQuestion(QuestionCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Question question = Question.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .locationKeyword(request.getLocationKeyword())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .mediaUrls(request.getMediaUrls())
                .build();

        questionRepository.save(question);
        return question.getId();
    }

    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getQuestions(String category, Pageable pageable) {
        Page<Question> questions = (category != null && !category.isBlank())
                ? questionRepository.findByCategory(category, pageable)
                : questionRepository.findAll(pageable);
        return questions.map(QuestionListResponse::new);
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponse getQuestionDetail(Long questionId, String email) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        boolean isLiked = false;
        if (email != null) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저 찾을 수 없음"));
            isLiked = questionLikeRepository.existsByQuestionAndUser(question, user);
        }

        return new QuestionDetailResponse(question, isLiked);
    }

    @Transactional
    public LikeResponse toggleLike(Long questionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        Optional<QuestionLike> existing = questionLikeRepository.findByQuestionAndUser(question, user);
        if (existing.isPresent()) {
            questionLikeRepository.delete(existing.get());
            question.decrementLike();
            return new LikeResponse(false, question.getLikeCount());
        } else {
            questionLikeRepository.save(QuestionLike.builder().question(question).user(user).build());
            question.incrementLike();
            return new LikeResponse(true, question.getLikeCount());
        }
    }
}
