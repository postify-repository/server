package com.example.Postify.service;

import com.example.Postify.domain.User;
import com.example.Postify.dto.UserLoginRequest;
import com.example.Postify.dto.UserLoginSuccessResponse;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.DuplicateEmailException;
import com.example.Postify.exception.DuplicateNicknameException;
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
                user.getEmail(),
                user.getNickname(),
                user.getDisplayName()
        );
    }

    // 회원가입
    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .displayName(request.getDisplayName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .shortBio(request.getShortBio())
                .profileImage(request.getProfileImage())
                .socialLinks(request.getSocialLinks())
                .build();

        userRepository.save(user);
    }

    // 회원 탈퇴
    public void deleteUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.", "password");
        }
        userRepository.delete(user);
    }

    // 로그아웃
    public void logout(String email) {

    }

    // 토큰 재발급
    public String refreshToken(String refreshToken) {
        return jwtUtil.refreshAccessToken(refreshToken);
    }


}
