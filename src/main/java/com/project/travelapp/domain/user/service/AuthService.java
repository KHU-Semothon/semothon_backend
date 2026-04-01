package com.project.travelapp.domain.user.service;

import com.project.travelapp.domain.user.dto.LoginRequest;
import com.project.travelapp.domain.user.dto.LoginResponse;
import com.project.travelapp.domain.user.dto.SignupRequest;
import com.project.travelapp.domain.user.entity.User;
import com.project.travelapp.domain.user.repository.UserRepository;
import com.project.travelapp.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signup(SignupRequest request) { // 반환 타입을 Long(userId)으로 변경
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
        return user.getId(); // 생성된 userId 반환
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) { // 반환 타입을 LoginResponse로 변경
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), "", Collections.emptyList()
        );

        String token = jwtTokenProvider.generateToken(authentication);

        // API 명세에 맞게 토큰, 유저ID, 닉네임 함께 반환
        return new LoginResponse(token, user.getId(), user.getNickname());
    }
}