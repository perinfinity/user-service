package com.perinfinity.auth_api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private String user;
    private String email;
    private String role;
    private String createdAt;
    private String country;
    private String city;
    private String profileImage;
    private List<String> preferredCategories;
}
