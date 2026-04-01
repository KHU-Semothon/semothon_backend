package com.project.travelapp.domain.answer.repository;

import com.project.travelapp.domain.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}