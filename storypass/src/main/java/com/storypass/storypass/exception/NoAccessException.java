package com.storypass.storypass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
public class NoAccessException extends RuntimeException {
    public NoAccessException(String message) {
        super(message);
    }
}
