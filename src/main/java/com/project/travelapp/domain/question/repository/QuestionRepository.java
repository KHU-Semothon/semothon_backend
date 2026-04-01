package com.project.travelapp.domain.question.repository;

import com.project.travelapp.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 페이징 처리된 질문 목록 조회 (최신순 등은 Pageable 객체에서 처리)
    Page<Question> findAll(Pageable pageable);
}