package com.perinfinity.auth_api.dtos;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RegisterUserDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String orgName;
    private String bio;
    private String address;
    private String phone;
    private String country;
    private String city;
    private String profileImage;

    @Size(max = 5, message = "Vous pouvez sélectionner au maximum 5 catégories")
    private List<String> preferredCategories = new ArrayList<>();
}
