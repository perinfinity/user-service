package com.perinfinity.auth_api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private String username;
    private String email;
    private String role;
    private String createdAt;
}
