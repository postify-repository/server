package com.example.Postify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequest {

    @NotBlank(message = "password는 필수 입력 항목입니다.")
    private String password;
}
