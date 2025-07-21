package com.example.Postify.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

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

    private String displayName; // 명세서에 따라 추가

    @Column(columnDefinition = "TEXT")
    private String shortBio; // bio → shortBio로 이름 수정

    @Column(nullable = true)
    private String provider;

    @Column(nullable = true)
    private String providerId;


    @ElementCollection
    @CollectionTable(name = "user_social_links", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
    private Map<String, String> socialLinks; // GitHub, Twitter 등

    @Builder
    public User(String username, String email, String nickname, String passwordHash,
                String profileImage, String displayName, String shortBio, Map<String, String> socialLinks, String provider, String providerId) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.profileImage = profileImage;
        this.displayName = displayName;
        this.shortBio = shortBio;
        this.socialLinks = socialLinks;
        this.provider = provider;
        this.providerId = providerId;
    }

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateProfile(String nickname, String bio, String profileImage) {
        this.nickname = nickname;
        this.shortBio = bio;
        this.profileImage = profileImage;
    }


}
