package com.example.Postify.dto;

import com.example.Postify.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {
    private String accessToken;
    private String username;
    private String email;
    private String nickname;
    private String userId;
    private String bio;

    public UserLoginResponse(User user, String accessToken, String username, String email, String nickname, String userId, String shortBio) {
        this.accessToken = accessToken;
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.userId = user.getId().toString();
        this.bio = user.getShortBio();
    }
}
