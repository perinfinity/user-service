package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.perinfinity.auth_api.dtos.*;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.service.AuthenticationService;
import com.perinfinity.auth_api.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody RegisterUserDto registerUserDto) {

        User registeredUser = authenticationService.signup(registerUserDto);

        if(Objects.isNull(registeredUser)) {

        }
        // Authenticate the newly created user
        LoginUserDto loginUser = new LoginUserDto();
        loginUser.setEmail(registerUserDto.getEmail());
        loginUser.setPassword(registerUserDto.getPassword());

        LoginResponseDto loginResponse = authenticateAndGenerateToken(loginUser);

        RegistrationResponseDto registrationResponseDto = new RegistrationResponseDto();
        registrationResponseDto.setMessage("User registration successfully");
        registrationResponseDto.setLoginResponse(loginResponse);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        registrationResponseDto.setUser(objectMapper
                .convertValue(registeredUser, UserResponseDto.class));
        return ResponseEntity.ok(registrationResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        LoginResponseDto loginResponse = authenticateAndGenerateToken(loginUserDto);

        return ResponseEntity.ok(loginResponse);
    }

    private LoginResponseDto authenticateAndGenerateToken(LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();
        return loginResponse;
    }
}
