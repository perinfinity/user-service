package com.perinfinity.auth_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeResponseDto {
    private String token;
    private String role;
    private String user;
    private String message;
    private boolean success;
}



