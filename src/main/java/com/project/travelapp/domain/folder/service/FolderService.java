package com.project.travelapp.domain.folder.service;

import com.project.travelapp.domain.folder.dto.FolderPinResponse;
import com.project.travelapp.domain.folder.dto.FolderResponse;
import com.project.travelapp.domain.folder.entity.Folder;
import com.project.travelapp.domain.folder.entity.FolderQuestion;
import com.project.travelapp.domain.folder.repository.FolderQuestionRepository;
import com.project.travelapp.domain.folder.repository.FolderRepository;
import com.project.travelapp.domain.question.dto.QuestionListResponse;
import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.repository.QuestionRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final FolderQuestionRepository folderQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createFolder(String name, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Folder folder = folderRepository.save(Folder.builder().name(name).user(user).build());
        return folder.getId();
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> getFolders(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return folderRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(FolderResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addQuestionToFolder(Long folderId, Long questionId, String email) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        // 내 폴더가 맞는지 권한 검증
        if (!folder.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 폴더에만 저장할 수 있습니다.");
        }

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        // 중복 스크랩 방지
        if (folderQuestionRepository.existsByFolderAndQuestion(folder, question)) {
            throw new IllegalArgumentException("이미 이 폴더에 저장된 게시물입니다.");
        }

        folderQuestionRepository.save(FolderQuestion.builder().folder(folder).question(question).build());
    }

    @Transactional(readOnly = true)
    public List<FolderPinResponse> getFolderPinsWithinBounds(Long folderId, Double minLat, Double maxLat, Double minLng, Double maxLng, String email) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        if (!folder.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 폴더만 조회할 수 있습니다.");
        }

        return folderQuestionRepository.findQuestionsInFolderWithinBounds(folderId, minLat, maxLat, minLng, maxLng).stream()
                .map(FolderPinResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionListResponse> getQuestionsInFolder(Long folderId, String email) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        // 본인 확인
        if (!folder.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 폴더만 조회할 수 있습니다.");
        }

        return folderQuestionRepository.findAllQuestionsByFolderId(folderId).stream()
                .map(QuestionListResponse::new)
                .collect(Collectors.toList());
    }
}