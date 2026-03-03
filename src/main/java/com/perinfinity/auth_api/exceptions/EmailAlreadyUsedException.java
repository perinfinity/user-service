package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyUsedException extends AuthApiException {

    public EmailAlreadyUsedException(String email) {
        super("Email '" + email + "' is already in use", HttpStatus.CONFLICT);
    }
}
