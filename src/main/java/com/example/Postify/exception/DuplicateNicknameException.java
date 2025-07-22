package com.example.Postify.exception;

public class DuplicateNicknameException extends RuntimeException {
    private final String field;

    public DuplicateNicknameException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
