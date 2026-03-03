package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthApiException {

    public UserNotFoundException(String email) {
        super("Aucun utilisateur trouvé avec l'email : " + email, HttpStatus.NOT_FOUND);
    }
}
