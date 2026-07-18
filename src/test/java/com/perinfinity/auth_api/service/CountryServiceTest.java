package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.CountryResponseDto;
import com.perinfinity.auth_api.entities.Country;
import com.perinfinity.auth_api.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private List<Country> activeCountries;

    @BeforeEach
    void setUp() {
        activeCountries = List.of(
                Country.builder().id(1L).code("CM").name("Cameroun").active(true).createdAt(LocalDateTime.now()).build(),
                Country.builder().id(2L).code("CI").name("Côte d'Ivoire").active(true).createdAt(LocalDateTime.now()).build(),
                Country.builder().id(3L).code("MU").name("Maurice").active(true).createdAt(LocalDateTime.now()).build()
        );
    }

    @Test
    void getActiveCountries_returnsAllActiveCountries() {
        when(countryRepository.findAllByActiveTrue()).thenReturn(activeCountries);

        List<CountryResponseDto> result = countryService.getActiveCountries();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(CountryResponseDto::getCode)
                .containsExactly("CM", "CI", "MU");
    }

    @Test
    void getActiveCountries_mapsFieldsCorrectly() {
        when(countryRepository.findAllByActiveTrue()).thenReturn(List.of(activeCountries.get(0)));

        List<CountryResponseDto> result = countryService.getActiveCountries();

        CountryResponseDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCode()).isEqualTo("CM");
        assertThat(dto.getName()).isEqualTo("Cameroun");
    }

    @Test
    void getActiveCountries_returnsEmptyList_whenNoActiveCountries() {
        when(countryRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<CountryResponseDto> result = countryService.getActiveCountries();

        assertThat(result).isEmpty();
    }
}
