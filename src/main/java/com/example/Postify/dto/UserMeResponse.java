package com.example.Postify.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMeResponse {
    private final Long id;
    private final String email;
    private final String username;
    private final String displayName;
    private final String profileImage;
    private final String shortBio;

    @Builder
    public UserMeResponse(Long id, String email, String username,
                          String displayName, String profileImage, String shortBio) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.profileImage = profileImage;
        this.shortBio = shortBio;
    }
}
