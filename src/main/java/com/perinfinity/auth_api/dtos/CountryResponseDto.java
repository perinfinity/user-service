package com.perinfinity.auth_api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountryResponseDto {
    private Long id;
    private String code;
    private String name;
}
