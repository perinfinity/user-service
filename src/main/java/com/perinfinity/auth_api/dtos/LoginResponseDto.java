package com.perinfinity.auth_api.dtos;

import com.perinfinity.auth_api.entities.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    private String token;

    private long expiresIn;

    private String user;

    private String role;

}
