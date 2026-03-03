package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class VerificationCodeExpiredException extends AuthApiException {

    public VerificationCodeExpiredException() {
        super("Verification code has expired", HttpStatus.UNAUTHORIZED);
    }
}
