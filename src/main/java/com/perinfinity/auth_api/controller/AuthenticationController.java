package com.perinfinity.auth_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.perinfinity.auth_api.dtos.*;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.service.AuthenticationService;
import com.perinfinity.auth_api.service.CookieService;
import com.perinfinity.auth_api.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final CookieService cookieService;

    ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationController(JwtService jwtService,
                                    AuthenticationService authenticationService,
                                    CookieService cookieService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.cookieService = cookieService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        LoginUserDto loginUser = new LoginUserDto();
        loginUser.setEmail(registerUserDto.getEmail());
        loginUser.setPassword(registerUserDto.getPassword());

        String jwtToken = authenticateAndGenerateToken(loginUser);
        ResponseCookie cookie = cookieService.createAuthCookie(jwtToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LoginResponseDto loginResponse = buildLoginResponse(registeredUser, jwtToken);

        RegistrationResponseDto registrationResponseDto = new RegistrationResponseDto();
        registrationResponseDto.setMessage("Inscription réussie");
        registrationResponseDto.setLoginResponse(loginResponse);
        registrationResponseDto.setUser(objectMapper.convertValue(registeredUser, UserResponseDto.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(registrationResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        ResponseCookie cookie = cookieService.createAuthCookie(jwtToken);

        LoginResponseDto loginResponse = buildLoginResponse(authenticatedUser, jwtToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = cookieService.clearAuthCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
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

    /**
     * Réinitialisation du mot de passe : l'utilisateur fournit l'email, le code
     * reçu par email (via /send-code) et le nouveau mot de passe.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<SendCodeResponseDto> resetPassword(@RequestBody ResetPasswordDto request) {
        authenticationService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());

        SendCodeResponseDto response = SendCodeResponseDto.builder()
                .message("Mot de passe réinitialisé avec succès")
                .success(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponseDto> verifyCode(@RequestBody VerifyCodeRequestDto request) {
        User authenticatedUser = authenticationService.authenticateWithCode(request.getEmail(), request.getCode());
        String jwtToken = jwtService.generateToken(authenticatedUser);
        ResponseCookie cookie = cookieService.createAuthCookie(jwtToken);

        VerifyCodeResponseDto response = VerifyCodeResponseDto.builder()
                .userId(authenticatedUser.getId())
                .role(authenticatedUser.getRole().name())
                .user(authenticatedUser.getUsername())
                .message("Authentification réussie")
                .success(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    private String authenticateAndGenerateToken(LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        return jwtService.generateToken(authenticatedUser);
    }

    private LoginResponseDto buildLoginResponse(User user, String jwtToken) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .user(user.getEmail())
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole().name())
                .build();
    }
}
