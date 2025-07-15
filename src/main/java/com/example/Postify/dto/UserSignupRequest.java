package com.example.Postify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Map;

@Getter
public class UserSignupRequest {

    @NotBlank(message = "username은 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "nickname은 필수 입력 항목입니다.")
    private String nickname;

    @NotBlank(message = "displayName은 필수 입력 항목입니다.")
    private String displayName;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "email은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "password는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    // 선택 항목
    private String shortBio;
    private String profileImage;
    private Map<String, String> socialLinks;
}
