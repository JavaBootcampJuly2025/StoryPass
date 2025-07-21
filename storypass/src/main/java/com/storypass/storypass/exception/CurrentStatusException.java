package com.storypass.storypass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class CurrentStatusException extends RuntimeException {
    public CurrentStatusException(String message) {
        super(message);
    }
}
