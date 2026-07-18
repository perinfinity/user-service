package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perinfinity.auth_api.dtos.UpdateProfileRequest;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.handler.GlobalExceptionHandler;
import com.perinfinity.auth_api.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VolunteerControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VolunteerController volunteerController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User sampleUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(volunteerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleUser = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .password("hashed")
                .role(Role.VOLUNTEER)
                .firstName("John")
                .lastName("Doe")
                .bio("Volunteer bio")
                .country("CM")
                .city("Yaoundé")
                .preferredCategories(new ArrayList<>(List.of("Education")))
                .build();
    }

    @Test
    void getProfile_returns200_whenUserExists() throws Exception {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/v1/volunteers/johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.city").value("Yaoundé"));
    }

    @Test
    void getProfileById_returns200_whenVolunteerExists() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/v1/volunteers/by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getProfileById_returns404_whenUserNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/volunteers/by-id/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfileById_returns404_whenUserIsNotAVolunteer() throws Exception {
        User org = User.builder()
                .id(5L).username("org").email("org@x.com").password("h")
                .role(Role.ORGANIZATION).build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(org));

        mockMvc.perform(get("/api/v1/volunteers/by-id/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfile_returns404_whenUserNotFound() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/volunteers/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyProfile_returns200_whenAuthenticated() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                sampleUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_VOLUNTEER")));

        mockMvc.perform(get("/api/v1/volunteers/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateProfile_returns200_whenAuthenticated() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setCity("Douala");

        var auth = new UsernamePasswordAuthenticationToken(
                sampleUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_VOLUNTEER")));

        mockMvc.perform(put("/api/v1/volunteers/me")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }
}
