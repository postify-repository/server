package com.example.Postify.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final String field;

    public UnauthorizedException(String message, String field) {
        super(message);
        this.field = field;
    }
}
