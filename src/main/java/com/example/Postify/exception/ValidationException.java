package com.example.Postify.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    private final List<Map<String, String>> fieldErrors;

    public ValidationException(String message, List<Map<String, String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
