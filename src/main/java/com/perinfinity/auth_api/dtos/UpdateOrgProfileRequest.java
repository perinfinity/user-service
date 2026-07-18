package com.perinfinity.auth_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrgProfileRequest {
    private String orgName;
    private String missionStatement;
    private String about;
    private String city;
    private String country;
    private String profileImage;
}
