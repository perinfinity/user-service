package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perinfinity.auth_api.dtos.LoginUserDto;
import com.perinfinity.auth_api.dtos.VerifyCodeRequestDto;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.service.AuthenticationService;
import com.perinfinity.auth_api.service.CookieService;
import com.perinfinity.auth_api.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private CookieService cookieService;

    @InjectMocks
    private AuthenticationController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User stubUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        stubUser = User.builder()
                .id(1L)
                .username("jdoe")
                .email("jdoe@example.com")
                .role(Role.VOLUNTEER)
                .build();
    }

    @Test
    void resetPassword_returns200_andDelegatesToService() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/reset-password")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jdoe@example.com\",\"code\":\"123456\",\"newPassword\":\"NewPass1!\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.success").value(true));

        org.mockito.Mockito.verify(authenticationService)
                .resetPassword("jdoe@example.com", "123456", "NewPass1!");
    }

    @Test
    void login_setsHttpOnlyCookieAndReturnsUserInfo() throws Exception {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("jdoe@example.com");
        loginDto.setPassword("secret");

        when(authenticationService.authenticate(any())).thenReturn(stubUser);
        when(jwtService.generateToken(stubUser)).thenReturn("jwt-token-value");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        when(cookieService.createAuthCookie("jwt-token-value"))
                .thenReturn(ResponseCookie.from(CookieService.COOKIE_NAME, "jwt-token-value")
                        .httpOnly(true).path("/").maxAge(3600).sameSite("Strict").build());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("auth_token=jwt-token-value")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(jsonPath("$.user").value("jdoe@example.com"))
                .andExpect(jsonPath("$.role").value("VOLUNTEER"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void logout_clearsCookieAndReturns204() throws Exception {
        when(cookieService.clearAuthCookie())
                .thenReturn(ResponseCookie.from(CookieService.COOKIE_NAME, "")
                        .httpOnly(true).path("/").maxAge(0).sameSite("Strict").build());

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    void verifyCode_setsHttpOnlyCookieAndReturnsUserInfo() throws Exception {
        VerifyCodeRequestDto request = new VerifyCodeRequestDto();
        request.setEmail("jdoe@example.com");
        request.setCode("123456");

        when(authenticationService.authenticateWithCode("jdoe@example.com", "123456"))
                .thenReturn(stubUser);
        when(jwtService.generateToken(stubUser)).thenReturn("jwt-verify-token");
        when(cookieService.createAuthCookie("jwt-verify-token"))
                .thenReturn(ResponseCookie.from(CookieService.COOKIE_NAME, "jwt-verify-token")
                        .httpOnly(true).path("/").maxAge(3600).sameSite("Strict").build());

        mockMvc.perform(post("/api/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("auth_token=jwt-verify-token")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.role").value("VOLUNTEER"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }
}
