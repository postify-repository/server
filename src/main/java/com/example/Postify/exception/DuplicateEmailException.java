package com.example.Postify.exception;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {
    private final String field;

    public DuplicateEmailException(String message, String field) {
        super(message);
        this.field = field;
    }
}