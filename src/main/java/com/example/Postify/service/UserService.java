package com.example.Postify.service;

import com.example.Postify.domain.User;
import com.example.Postify.dto.UserSignupRequest;
import com.example.Postify.exception.BadRequestException;
import com.example.Postify.exception.DuplicateEmailException;
import com.example.Postify.exception.DuplicateNicknameException;
import com.example.Postify.exception.UserNotFoundException;
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

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다: " + email));
    }


    // 회원 탈퇴
    public void deleteUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadRequestException("비밀번호가 올바르지 않습니다.", "password");
        }
        userRepository.delete(user);
    }
}
