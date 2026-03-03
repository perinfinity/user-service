package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.SendCodeRequestDto;
import com.perinfinity.auth_api.dtos.SendCodeResponseDto;
import com.perinfinity.auth_api.dtos.VerifyCodeRequestDto;
import com.perinfinity.auth_api.dtos.VerifyCodeResponseDto;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.service.JwtService;
import com.perinfinity.auth_api.service.OrganizationAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("old/api/auth")
@RestController
public class OrganizationAuthController {
    /* 
    private final OrganizationAuthService organizationAuthService;
    private final JwtService jwtService;
    
    public OrganizationAuthController(OrganizationAuthService organizationAuthService, JwtService jwtService) {
        this.organizationAuthService = organizationAuthService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/send-code")
    public ResponseEntity<SendCodeResponseDto> sendCode(@RequestBody SendCodeRequestDto request) {
        try {
            organizationAuthService.sendCodeForOrganization(request);
            
            SendCodeResponseDto response = SendCodeResponseDto.builder()
                    .message("Code de vérification envoyé avec succès")
                    .success(true)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SendCodeResponseDto response = SendCodeResponseDto.builder()
                    .message("Erreur lors de l'envoi du code: " + e.getMessage())
                    .success(false)
                    .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponseDto> verifyCode(@RequestBody VerifyCodeRequestDto request) {
        try {
            User authenticatedUser = organizationAuthService.verifyCodeForOrganization(request);
            
            String jwtToken = jwtService.generateToken(authenticatedUser);
            
            VerifyCodeResponseDto response = VerifyCodeResponseDto.builder()
                    .token(jwtToken)
                    .message("Authentification réussie")
                    .success(true)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            VerifyCodeResponseDto response = VerifyCodeResponseDto.builder()
                    .token(null)
                    .message("Code de vérification invalide")
                    .success(false)
                    .build();
            
            return ResponseEntity.status(401).body(response);
        }
    }*/
}

