package com.example.Postify.exception;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String message) {
        super(message);
    }
}