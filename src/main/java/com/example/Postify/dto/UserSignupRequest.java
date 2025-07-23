package com.example.Postify.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String username;
    private String email;
    private String password;
    private String nickname;
    private String bio;
}
