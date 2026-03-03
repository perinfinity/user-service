package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidVerificationCodeException extends AuthApiException {

    public InvalidVerificationCodeException() {
        super("Code de vérification invalide", HttpStatus.UNAUTHORIZED);
    }
}
