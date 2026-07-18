package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.CountryResponseDto;
import com.perinfinity.auth_api.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryResponseDto>> getActiveCountries() {
        return ResponseEntity.ok(countryService.getActiveCountries());
    }
}
