package com.example.Postify.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {
    private final String field;

    public InternalServerException(String message) {
        super(message);
        this.field = null;
    }

    public InternalServerException(String message, String field) {
        super(message);
        this.field = field;
    }
}