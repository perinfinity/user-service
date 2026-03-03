package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRoleException extends AuthApiException {

    public InvalidRoleException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
