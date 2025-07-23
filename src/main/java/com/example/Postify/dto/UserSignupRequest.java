package com.example.Postify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserSignupRequest {

    @NotBlank(message = "username은 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "email은 필수 입력 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "password는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "nickname은 필수 입력 항목입니다.")
    private String nickname;

    private String bio; // 선택값
}
