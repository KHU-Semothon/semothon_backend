package com.project.travelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.travelapp.domain.pin.entity.Pin;
import com.project.travelapp.domain.pin.repository.PinRepository;
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
@Transactional
class PinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder().email("pin@test.com").password("pwd").nickname("핀마스터").build();
        userRepository.save(testUser);

        accessToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), "", Collections.emptyList()));
    }

    @Test
    @DisplayName("4-1. 지도 핀 등록 테스트 (명세서 규격 일치 확인)")
    void createPinTest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("latitude", 34.6687);
        request.put("longitude", 135.5013);
        request.put("pinType", "DANGER");
        request.put("title", "밤 시간대 삐끼 주의 구역");
        request.put("description", "밤 10시 이후 유흥업소 호객행위가 매우 심함.");

        mockMvc.perform(post("/api/v1/pins")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                // 👇 변경된 메시지 검증
                .andExpect(jsonPath("$.message").value("지도 정보가 등록되었습니다."))
                // 👇 data 안에 pinId가 객체 형태로 잘 들어있는지 검증
                .andExpect(jsonPath("$.data.pinId").isNumber());
    }

    @Test
    @DisplayName("4-2. Bounding Box 기반 핀 조회 테스트 (인증 불필요, 명세서 규격 확인)")
    void getPinsInBoundingBoxTest() throws Exception {
        // given: 핀 2개 등록 (하나는 박스 안, 하나는 박스 밖)
        Pin pinInBox = Pin.builder().user(testUser).latitude(34.6650).longitude(135.5030)
                .pinType("RESTAURANT").title("현지인 추천 타코야끼").build();
        Pin pinOutBox = Pin.builder().user(testUser).latitude(40.0).longitude(145.0)
                .pinType("ETC").title("박스 밖").build();

        pinRepository.save(pinInBox);
        pinRepository.save(pinOutBox);

        // when & then: 오사카 도톤보리 주변 Bounding Box
        mockMvc.perform(get("/api/v1/pins?minLat=34.660&maxLat=34.670&minLng=135.500&maxLng=135.510"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("핀 목록 조회 성공"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("현지인 추천 타코야끼"))
                .andExpect(jsonPath("$.data[0].pinType").value("RESTAURANT"))
                // 👇 다이어트된 응답 객체이므로 description 같은 필드가 없어야 정상임을 검증
                .andExpect(jsonPath("$.data[0].description").doesNotExist());
    }
}