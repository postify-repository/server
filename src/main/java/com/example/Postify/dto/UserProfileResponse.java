package com.example.Postify.dto;

import com.example.Postify.domain.User;
import lombok.Getter;

import java.util.Map;

@Getter
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String shortbio;  // User는 shortBio 필드 사용 중
    private String profileImage;

    public UserProfileResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.shortbio = user.getShortBio(); // shortBio → bio 로 매핑
        this.profileImage = user.getProfileImage();
    }
}
