package com.project.travelapp.domain.question.repository;

import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.entity.QuestionLike;
import com.project.travelapp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {
    Optional<QuestionLike> findByQuestionAndUser(Question question, User user);
    boolean existsByQuestionAndUser(Question question, User user);
}
