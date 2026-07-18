package com.perinfinity.auth_api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VolunteerProfileDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private String country;
    private String city;
    private String profileImage;
    private List<String> preferredCategories;
}
