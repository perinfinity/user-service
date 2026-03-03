package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyUsedException extends AuthApiException {

    public EmailAlreadyUsedException(String email) {
        super("L'email '" + email + "' est déjà utilisé par un autre utilisateur", HttpStatus.CONFLICT);
    }
}
