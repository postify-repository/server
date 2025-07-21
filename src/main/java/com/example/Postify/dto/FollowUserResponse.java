package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowUserResponse {
    private Long userId;
    private String nickname;
    private String profileImage;
    private String bio;
}
