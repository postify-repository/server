package com.example.Postify.dto;

public class SocialRegisterRequest {

    private final String provider;
    private final String username;
    private final String nickname;
    private final String bio;

    public SocialRegisterRequest(String provider, String username, String nickname, String bio) {
        this.provider = provider;
        this.username = username;
        this.nickname = nickname;
        this.bio = bio;
    }

    public String getProvider() {
        return provider;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }
}
