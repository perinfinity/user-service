package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.CountryResponseDto;
import com.perinfinity.auth_api.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<CountryResponseDto> getActiveCountries() {
        return countryRepository.findAllByActiveTrue().stream()
                .map(c -> CountryResponseDto.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .name(c.getName())
                        .build())
                .toList();
    }
}
