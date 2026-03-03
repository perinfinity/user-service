package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.perinfinity.auth_api.dtos.*;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.service.AuthenticationService;
import com.perinfinity.auth_api.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/auth")
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

        LoginUserDto loginUser = new LoginUserDto();
        loginUser.setEmail(registerUserDto.getEmail());
        loginUser.setPassword(registerUserDto.getPassword());

        LoginResponseDto loginResponse = authenticateAndGenerateToken(loginUser);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RegistrationResponseDto registrationResponseDto = new RegistrationResponseDto();
        registrationResponseDto.setMessage("Inscription réussie");
        registrationResponseDto.setLoginResponse(loginResponse);
        registrationResponseDto.setUser(objectMapper.convertValue(registeredUser, UserResponseDto.class));

        return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        LoginResponseDto loginResponse = authenticateAndGenerateToken(loginUserDto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/send-code")
    public ResponseEntity<SendCodeResponseDto> sendCode(@RequestBody SendCodeRequestDto request) {
        authenticationService.sendVerificationCode(request.getEmail());

        SendCodeResponseDto response = SendCodeResponseDto.builder()
                .message("Code de vérification envoyé avec succès")
                .success(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponseDto> verifyCode(@RequestBody VerifyCodeRequestDto request) {
        User authenticatedUser = authenticationService.authenticateWithCode(request.getEmail(), request.getCode());

        String jwtToken = jwtService.generateToken(authenticatedUser);

        VerifyCodeResponseDto response = VerifyCodeResponseDto.builder()
                .token(jwtToken)
                .role(authenticatedUser.getRole().name())
                .user(authenticatedUser.getUsername())
                .message("Authentification réussie")
                .success(true)
                .build();

        return ResponseEntity.ok(response);
    }

    private LoginResponseDto authenticateAndGenerateToken(LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        return LoginResponseDto.builder()
                .token(jwtToken)
                .user(authenticatedUser.getEmail())
                .expiresIn(jwtService.getExpirationTime())
                .role(authenticatedUser.getRole().name())
                .build();
    }
}
