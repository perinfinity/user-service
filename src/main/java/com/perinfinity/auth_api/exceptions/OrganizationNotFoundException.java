package com.perinfinity.auth_api.exceptions;

import org.springframework.http.HttpStatus;

public class OrganizationNotFoundException extends AuthApiException {

    public OrganizationNotFoundException(Long id) {
        super("Organization not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
