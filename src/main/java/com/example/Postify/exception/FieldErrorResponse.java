package com.example.Postify.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FieldErrorResponse {
    private String error;
    private String message;
    private List<FieldMessage> fields;

    @Getter
    @AllArgsConstructor
    public static class FieldMessage {
        private String field;
        private String message;
    }
}
