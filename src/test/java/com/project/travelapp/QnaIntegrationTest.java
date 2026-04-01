package com.project.travelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.travelapp.domain.question.entity.Question;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 완료 후 DB 롤백!
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
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 유저 생성 및 DB 저장
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();
        userRepository.save(testUser);

        // 2. 해당 유저의 JWT 토큰 발급
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), "", Collections.emptyList());
        accessToken = jwtTokenProvider.generateToken(auth);
    }

    @Test
    @DisplayName("2-1. 질문 작성 성공 테스트")
    void createQuestionTest() throws Exception {
        // given
        Map<String, String> request = new HashMap<>();
        request.put("title", "오사카 밤 11시 이후 도톤보리 안전한가요?");
        request.put("content", "혼자 여행 가는데 걱정입니다.");
        request.put("category", "DANGER");
        request.put("locationKeyword", "오사카 도톤보리");

        // when & then
        mockMvc.perform(post("/api/v1/questions")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("질문이 등록되었습니다."))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("2-2. 질문 목록 페이징 조회 테스트 (인증 불필요)")
    void getQuestionListTest() throws Exception {
        // given: 질문 데이터 1개 미리 세팅
        Question q = Question.builder().user(testUser).title("제목").content("내용").category("ETC").build();
        questionRepository.save(q);

        // when & then
        mockMvc.perform(get("/api/v1/questions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].title").value("제목"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("2-4. 답변 작성 성공 테스트")
    void createAnswerTest() throws Exception {
        // given: 질문이 하나 존재해야 함
        Question q = Question.builder().user(testUser).title("질문").content("내용").category("ETC").build();
        questionRepository.save(q);

        Map<String, String> request = new HashMap<>();
        request.put("content", "네 메인거리는 안전합니다!");

        // when & then
        mockMvc.perform(post("/api/v1/questions/" + q.getId() + "/answers")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("답변이 등록되었습니다."))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }
}