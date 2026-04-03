package com.project.travelapp.domain.folder.repository;

import com.project.travelapp.domain.folder.entity.Folder;
import com.project.travelapp.domain.folder.entity.FolderQuestion;
import com.project.travelapp.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FolderQuestionRepository extends JpaRepository<FolderQuestion, Long> {

    // 이미 폴더에 담은 게시물인지 중복 확인
    boolean existsByFolderAndQuestion(Folder folder, Question question);

    // [핵심] 바운딩 박스 내의 질문들만 조회
    @Query("SELECT fq.question FROM FolderQuestion fq " +
            "WHERE fq.folder.id = :folderId " +
            "AND fq.question.latitude BETWEEN :minLat AND :maxLat " +
            "AND fq.question.longitude BETWEEN :minLng AND :maxLng")
    List<Question> findQuestionsInFolderWithinBounds(
            @Param("folderId") Long folderId,
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );

    @Query("SELECT fq.question FROM FolderQuestion fq " +
            "WHERE fq.folder.id = :folderId " +
            "ORDER BY fq.question.createdAt DESC")
    List<Question> findAllQuestionsByFolderId(@Param("folderId") Long folderId);
}