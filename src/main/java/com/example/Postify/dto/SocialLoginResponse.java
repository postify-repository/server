package com.example.Postify.dto;

public class SocialLoginResponse {

    private final String accessToken;
    private final String username;
    private final String email;
    private final String nickname;
    private final String userId;
    private final String bio;

    public SocialLoginResponse(String accessToken, String username, String email,
                               String nickname, String userId, String bio) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.userId = userId;
        this.bio = bio;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserId() {
        return userId;
    }

    public String getBio() {
        return bio;
    }
}
