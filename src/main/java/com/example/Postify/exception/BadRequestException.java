// 📁 src/main/java/com/example/Postify/exception/BadRequestException.java

package com.example.Postify.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private final String field;

    public BadRequestException(String message, String field) {
        super(message);
        this.field = field;
    }


}
