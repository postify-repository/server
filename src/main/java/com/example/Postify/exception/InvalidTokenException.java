package com.example.Postify.exception;

public class InvalidTokenException extends RuntimeException {
    private final String field;

    public InvalidTokenException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
