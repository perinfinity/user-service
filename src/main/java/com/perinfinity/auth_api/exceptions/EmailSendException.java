package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailSendException extends AuthApiException {

    public EmailSendException(String recipient, Throwable cause) {
        super("Échec de l'envoi du code de vérification à : " + recipient, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
