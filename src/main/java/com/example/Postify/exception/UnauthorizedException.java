package com.example.Postify.exception;

public class UnauthorizedException extends RuntimeException {
    private String field;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
