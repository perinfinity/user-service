package com.perinfinity.auth_api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrgProfileDto {
    private Long id;
    private String orgName;
    private String missionStatement;
    private String about;
    private boolean verifiedBadge;
    private int completenessScore;
    private String city;
    private String country;
    private String profileImage;
}
