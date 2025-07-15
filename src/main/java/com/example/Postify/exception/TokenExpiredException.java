package com.example.Postify.exception;

public class TokenExpiredException extends RuntimeException {
    private final String field;

    public TokenExpiredException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
