package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public abstract class AuthApiException extends RuntimeException {

    private final HttpStatus status;

    public AuthApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AuthApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
