package com.example.Postify.service;

import com.example.Postify.domain.User;
import com.example.Postify.dto.UserLoginRequest;
import com.example.Postify.dto.UserLoginSuccessResponse;
import com.example.Postify.exception.*;
import com.example.Postify.dto.UserSignupRequest;
import com.example.Postify.jwt.JwtUtil;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 로그인
    public UserLoginSuccessResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("존재하지 않는 이메일입니다.", "email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.", "password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserLoginSuccessResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getId().toString(),
                user.getShortBio()
        );
    }

    // 로그아웃
    public void logout(String email) {

    }

    // 토큰 재발급
    public String refreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 refresh token입니다.", "refreshToken");
        }

        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new TokenExpiredException("refresh token이 만료되었습니다.", "refreshToken");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        return jwtUtil.generateToken(email); // 새 accessToken 생성
    }





}
