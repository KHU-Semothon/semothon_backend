package com.project.travelapp.domain.question.service;

import com.project.travelapp.domain.question.dto.QuestionCreateRequest;
import com.project.travelapp.domain.question.dto.QuestionDetailResponse;
import com.project.travelapp.domain.question.dto.QuestionListResponse;
import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.repository.QuestionRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
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
                .build();

        questionRepository.save(question);
        return question.getId();
    }

    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionListResponse::new);
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponse getQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        return new QuestionDetailResponse(question);
    }
}