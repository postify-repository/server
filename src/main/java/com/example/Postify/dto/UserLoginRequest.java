package com.example.Postify.dto;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String email;
    private String password;
}