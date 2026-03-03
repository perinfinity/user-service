package com.perinfinity.auth_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
    private String email;
    private String code;
    private String newPassword;
}
