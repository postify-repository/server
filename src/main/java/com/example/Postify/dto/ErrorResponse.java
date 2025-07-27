package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private String field;

    // 필드 없는 오류 응답에 사용하는 생성자
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.field = null;
    }
}
