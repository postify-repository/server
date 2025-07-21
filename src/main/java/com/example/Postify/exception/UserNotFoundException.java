package com.example.Postify.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
    }
}
