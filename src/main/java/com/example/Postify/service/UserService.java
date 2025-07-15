package com.example.Postify.service;

import com.example.Postify.domain.User;
import com.example.Postify.dto.UserSignupRequest;
import com.example.Postify.exception.DuplicateEmailException;
import com.example.Postify.exception.DuplicateNicknameException;
import com.example.Postify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .displayName(request.getDisplayName())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .shortBio(request.getShortBio())
                .profileImage(request.getProfileImage())
                .socialLinks(request.getSocialLinks())
                .build();

        // 저장
        userRepository.save(user);
    }

    public void deleteUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }
}
