package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.CountryResponseDto;

import java.util.List;

public interface CountryService {

    List<CountryResponseDto> getActiveCountries();
}
