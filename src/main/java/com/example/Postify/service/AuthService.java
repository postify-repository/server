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

    /**
     * 로그인 로직
     */
    public User login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }


    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    public void logout(String email) {
        // stateless JWT 구조에서는 실제 DB 연산 없음
    }

    public String refreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 refresh token입니다.", "refreshToken");
        }

        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new TokenExpiredException("refresh token이 만료되었습니다.", "refreshToken");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        return jwtUtil.generateToken(email);
    }
}



