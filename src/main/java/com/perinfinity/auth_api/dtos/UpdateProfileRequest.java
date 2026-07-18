package com.perinfinity.auth_api.dtos;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private String phone;
    private String country;
    private String city;
    private String address;
    private String profileImage;

    @Size(max = 5, message = "Maximum 5 preferred categories allowed")
    private List<String> preferredCategories;
}
