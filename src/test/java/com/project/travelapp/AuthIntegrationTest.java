package com.project.travelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import com.project.travelapp.global.auth.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("1-1. 회원가입 성공 테스트")
    void signupTest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "newuser@test.com");
        request.put("password", "password123!");
        request.put("nickname", "뉴비");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.userId").isNotEmpty());
    }

    @Test
    @DisplayName("1-2. 로그인 성공 테스트")
    void loginTest() throws Exception {
        // given: 유저 미리 생성
        User user = User.builder()
                .email("login@test.com")
                .password(passwordEncoder.encode("password123!"))
                .nickname("로거")
                .build();
        userRepository.save(user);

        Map<String, String> request = new HashMap<>();
        request.put("email", "login@test.com");
        request.put("password", "password123!");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.nickname").value("로거"));
    }

    @Test
    @DisplayName("1-3. 내 프로필 조회 테스트")
    void getMyProfileTest() throws Exception {
        // given: 유저 생성 및 토큰 발급
        User user = User.builder().email("me@test.com").password("pwd").nickname("나야나").build();
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(user.getEmail(), "", Collections.emptyList()));

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("나야나"))
                .andExpect(jsonPath("$.data.trustScore").value(0));
    }
}