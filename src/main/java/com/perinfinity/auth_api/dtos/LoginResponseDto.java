package com.perinfinity.auth_api.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
}
