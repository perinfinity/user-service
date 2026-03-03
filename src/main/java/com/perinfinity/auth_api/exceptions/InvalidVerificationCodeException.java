package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidVerificationCodeException extends AuthApiException {

    public InvalidVerificationCodeException() {
        super("Invalid verification code", HttpStatus.UNAUTHORIZED);
    }
}
