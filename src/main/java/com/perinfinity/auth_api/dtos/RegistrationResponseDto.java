package com.perinfinity.auth_api.dtos;

import lombok.Data;

@Data
public class RegistrationResponseDto {
    private String message;
    private UserResponseDto user;
    private LoginResponseDto loginResponse;
}
