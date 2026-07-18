package com.perinfinity.auth_api.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {

    private Long userId;

    private long expiresIn;

    private String user;

    private String role;
}
