package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailSendException extends AuthApiException {

    public EmailSendException(String recipient, Throwable cause) {
        super("Failed to send verification code to: " + recipient, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
