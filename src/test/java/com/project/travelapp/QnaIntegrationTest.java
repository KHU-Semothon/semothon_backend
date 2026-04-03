package com.project.travelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.travelapp.domain.answer.entity.Answer;
import com.project.travelapp.domain.answer.repository.AnswerRepository;
import com.project.travelapp.domain.question.entity.Question;
import com.project.travelapp.domain.question.entity.QuestionLike;
import com.project.travelapp.domain.question.repository.QuestionLikeRepository;
import com.project.travelapp.domain.question.repository.QuestionRepository;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import com.project.travelapp.global.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QnaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionLikeRepository questionLikeRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private User otherUser;
    private String accessToken;
    private String otherToken;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build());

        otherUser = userRepository.save(User.builder()
                .email("other@example.com")
                .password("encodedPassword")
                .nickname("타인")
                .build());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), "", Collections.emptyList());
        accessToken = jwtTokenProvider.generateToken(auth);

        UsernamePasswordAuthenticationToken otherAuth =
                new UsernamePasswordAuthenticationToken(otherUser.getEmail(), "", Collections.emptyList());
        otherToken = jwtTokenProvider.generateToken(otherAuth);
    }

    @Test
    @DisplayName("2-1. 질문 작성 성공 - mediaUrls 포함")
    void createQuestionTest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "오사카 밤 11시 이후 도톤보리 안전한가요?");
        request.put("content", "혼자 여행 가는데 걱정입니다.");
        request.put("category", "DANGER");
        request.put("locationKeyword", "오사카 도톤보리");
        request.put("mediaUrls", List.of("http://daramjwi.com/uploads/uuid1_osaka-night.jpg"));

        mockMvc.perform(post("/api/v1/questions")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("질문이 등록되었습니다."))
                .andExpect(jsonPath("$.data.questionId").isNumber());
    }

    @Test
    @DisplayName("2-1. 질문 작성 실패 - 인증 없음")
    void createQuestionUnauthorizedTest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("title", "제목");
        request.put("content", "내용");
        request.put("category", "ETC");

        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2-2. 질문 목록 조회 - 전체 페이징")
    void getQuestionListTest() throws Exception {
        questionRepository.save(Question.builder()
                .user(testUser).title("제목").content("내용").category("ETC")
                .mediaUrls(List.of("http://example.com/thumb.jpg"))
                .build());

        mockMvc.perform(get("/api/v1/questions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].title").value("제목"))
                .andExpect(jsonPath("$.data.content[0].likeCount").value(0))
                .andExpect(jsonPath("$.data.content[0].thumbnailUrl").value("http://example.com/thumb.jpg"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("2-2. 질문 목록 조회 - 카테고리 필터")
    void getQuestionListByCategoryTest() throws Exception {
        questionRepository.save(Question.builder()
                .user(testUser).title("위험 질문").content("내용").category("DANGER").build());
        questionRepository.save(Question.builder()
                .user(testUser).title("가격 질문").content("내용").category("PRICE").build());

        mockMvc.perform(get("/api/v1/questions?category=DANGER&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].category").value("DANGER"));
    }

    @Test
    @DisplayName("2-3. 질문 상세 조회 - 비회원 (isLiked=false)")
    void getQuestionDetailAnonymousTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("도톤보리 안전?").content("걱정돼요").category("DANGER")
                .mediaUrls(List.of("http://example.com/img.jpg"))
                .build());

        mockMvc.perform(get("/api/v1/questions/" + q.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionId").value(q.getId()))
                .andExpect(jsonPath("$.data.likeCount").value(0))
                .andExpect(jsonPath("$.data.isLiked").value(false))
                .andExpect(jsonPath("$.data.mediaUrls[0]").value("http://example.com/img.jpg"))
                .andExpect(jsonPath("$.data.answers").isArray());
    }

    @Test
    @DisplayName("2-3. 질문 상세 조회 - 로그인 유저 (isLiked 반영)")
    void getQuestionDetailWithLikeTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        questionLikeRepository.save(QuestionLike.builder().question(q).user(otherUser).build());
        q.incrementLike();

        mockMvc.perform(get("/api/v1/questions/" + q.getId())
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.isLiked").value(true));
    }

    @Test
    @DisplayName("2-4. 답변 작성 성공 - mediaUrls 포함")
    void createAnswerTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        Map<String, Object> request = new HashMap<>();
        request.put("content", "네 메인거리는 안전합니다!");
        request.put("mediaUrls", List.of("http://example.com/safe.jpg"));

        mockMvc.perform(post("/api/v1/questions/" + q.getId() + "/answers")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("답변이 등록되었습니다."))
                .andExpect(jsonPath("$.data.answerId").isNumber());
    }

    @Test
    @DisplayName("2-5. 답변 채택 성공")
    void acceptAnswerTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        Answer answer = answerRepository.save(Answer.builder()
                .question(q).user(otherUser).content("좋은 답변입니다.").build());

        mockMvc.perform(post("/api/v1/answers/" + answer.getId() + "/accept")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("답변이 채택되었습니다."));
    }

    @Test
    @DisplayName("2-5. 답변 채택 실패 - 질문 작성자가 아님")
    void acceptAnswerForbiddenTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        Answer answer = answerRepository.save(Answer.builder()
                .question(q).user(otherUser).content("답변").build());

        mockMvc.perform(post("/api/v1/answers/" + answer.getId() + "/accept")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2-7. 좋아요 토글 - 좋아요 추가")
    void toggleLikeAddTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        mockMvc.perform(post("/api/v1/questions/" + q.getId() + "/like")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andExpect(jsonPath("$.data.likeCount").value(1));
    }

    @Test
    @DisplayName("2-7. 좋아요 토글 - 좋아요 취소")
    void toggleLikeCancelTest() throws Exception {
        Question q = questionRepository.save(Question.builder()
                .user(testUser).title("질문").content("내용").category("ETC").build());

        questionLikeRepository.save(QuestionLike.builder().question(q).user(otherUser).build());
        q.incrementLike();

        mockMvc.perform(post("/api/v1/questions/" + q.getId() + "/like")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLiked").value(false))
                .andExpect(jsonPath("$.data.likeCount").value(0));
    }

    @Test
    @DisplayName("2-6. 미디어 파일 업로드 성공")
    void uploadMediaTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-content".getBytes());

        mockMvc.perform(multipart("/api/v1/media/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("미디어 업로드 성공"))
                .andExpect(jsonPath("$.data.uploadedUrls[0]").isString());
    }
}
