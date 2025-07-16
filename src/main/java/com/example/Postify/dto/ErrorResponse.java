package com.example.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private String field;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}