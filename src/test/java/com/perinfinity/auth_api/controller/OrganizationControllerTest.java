package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perinfinity.auth_api.dtos.OrgProfileDto;
import com.perinfinity.auth_api.dtos.UpdateOrgProfileRequest;
import com.perinfinity.auth_api.exceptions.OrganizationNotFoundException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.handler.GlobalExceptionHandler;
import com.perinfinity.auth_api.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationController organizationController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrgProfileDto sampleDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(organizationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleDto = OrgProfileDto.builder()
                .id(10L)
                .orgName("My Org")
                .missionStatement("Help everyone")
                .about("We do good")
                .verifiedBadge(false)
                .completenessScore(100)
                .city("Paris")
                .country("FR")
                .build();
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/organizations/{id}
    // -------------------------------------------------------------------------

    @Test
    void getOrgById_returns200_whenOrgExists() throws Exception {
        when(organizationService.getOrgById(10L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/organizations/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.orgName").value("My Org"))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.completenessScore").value(100));
    }

    @Test
    void getOrgById_returns404_whenOrgNotFound() throws Exception {
        when(organizationService.getOrgById(99L))
                .thenThrow(new OrganizationNotFoundException(99L));

        mockMvc.perform(get("/api/v1/organizations/99"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/organizations/me
    // -------------------------------------------------------------------------

    @Test
    void updateMyProfile_returns200_whenAuthenticated() throws Exception {
        when(organizationService.updateMyProfile(eq("org@example.com"), any(UpdateOrgProfileRequest.class)))
                .thenReturn(sampleDto);

        UpdateOrgProfileRequest request = new UpdateOrgProfileRequest();
        request.setOrgName("My Org");
        request.setCity("Paris");

        var auth = new UsernamePasswordAuthenticationToken(
                "org@example.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZATION")));

        mockMvc.perform(put("/api/v1/organizations/me")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgName").value("My Org"))
                .andExpect(jsonPath("$.completenessScore").value(100));
    }
}
