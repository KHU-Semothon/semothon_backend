package com.project.travelapp.domain.answer.service;

import com.project.travelapp.domain.answer.dto.AnswerCreateRequest;
import com.project.travelapp.domain.answer.entity.Answer;
import com.project.travelapp.domain.answer.repository.AnswerRepository;
import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.repository.QuestionRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createAnswer(Long questionId, AnswerCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        Answer answer = Answer.builder()
                .user(user)
                .question(question)
                .content(request.getContent())
                .build();

        answerRepository.save(answer);
        return answer.getId();
    }

    @Transactional
    public void acceptAnswer(Long answerId, String requesterEmail) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));
        Question question = answer.getQuestion();

        // 1. 요청자가 질문의 작성자인지 확인
        if (!question.getUser().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("질문 작성자만 답변을 채택할 수 있습니다.");
        }
        // 2. 이미 채택된 답변인지 확인
        if (answer.getIsAccepted()) {
            throw new IllegalArgumentException("이미 채택된 답변입니다.");
        }

        // 3. 답변 채택 처리 및 작성자 신뢰도 상승 (+10점)
        answer.accept();
        answer.getUser().addTrustScore(10);
    }
}