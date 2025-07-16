package com.example.Postify.exception;

public class MissingFieldException extends RuntimeException {
    private final String field;

    public MissingFieldException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
