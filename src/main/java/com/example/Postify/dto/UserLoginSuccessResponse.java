package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginSuccessResponse {
    private String accessToken;
    private String email;
    private String nickname;
    private String displayName;
}
