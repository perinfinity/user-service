package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidJwtKeyException extends AuthApiException {

    public InvalidJwtKeyException(Throwable cause) {
        super("Clé secrète JWT invalide. Vérifiez que la clé est encodée en Base64 valide.", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
