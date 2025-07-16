package com.example.Postify.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // 로그인 ID

    @Column(nullable = false, unique = true)
    private String email;     // 회원가입 이메일

    @Column(nullable = false, unique = true)
    private String nickname;  // 닉네임

    @Column(nullable = false)
    private String passwordHash;  // 암호화된 비밀번호

    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Builder
    public User(String username, String email, String nickname, String passwordHash, String profileImage, String bio) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.profileImage = profileImage;
        this.bio = bio;
    }
}

// @Getter + @NoArgsConstructor : 롬복으로 생성자 + 접근 보호
// @Builder : 테스트/서비스 계층에서 객체 생성시 가독성 올라감
// @Column(nullable = false, unique = true) : DB 제약조건 확실히 설정(중복 방지)
// password → passwordHash : 보안상 해시 저장임을 명확히 표현