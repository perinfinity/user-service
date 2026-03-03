package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidJwtKeyException extends AuthApiException {

    public InvalidJwtKeyException(Throwable cause) {
        super("Invalid JWT secret key. Ensure the key is a valid Base64-encoded string.", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
