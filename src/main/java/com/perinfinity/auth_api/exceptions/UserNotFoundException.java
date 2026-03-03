package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthApiException {

    public UserNotFoundException(String email) {
        super("No user found with email: " + email, HttpStatus.NOT_FOUND);
    }
}
