package com.example.Postify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    @NotBlank(message = "nickname은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이어야 합니다.")
    private String nickname;

    @Size(max = 200, message = "소개글은 200자 이하여야 합니다.")
    private String bio;

    private String profileImage;
}
