package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.CountryResponseDto;
import com.perinfinity.auth_api.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    void getActiveCountries_returns200WithList() throws Exception {
        List<CountryResponseDto> countries = List.of(
                CountryResponseDto.builder().id(1L).code("CM").name("Cameroun").build(),
                CountryResponseDto.builder().id(2L).code("CI").name("Côte d'Ivoire").build(),
                CountryResponseDto.builder().id(3L).code("MU").name("Maurice").build()
        );
        when(countryService.getActiveCountries()).thenReturn(countries);

        mockMvc.perform(get("/api/v1/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].code").value("CM"))
                .andExpect(jsonPath("$[1].code").value("CI"))
                .andExpect(jsonPath("$[2].code").value("MU"));
    }

    @Test
    void getActiveCountries_returnsEmptyList_whenNoCountries() throws Exception {
        when(countryService.getActiveCountries()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
