package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.SendCodeRequestDto;
import com.perinfinity.auth_api.dtos.VerifyCodeRequestDto;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.InvalidRoleException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationAuthService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public void sendCodeForOrganization(SendCodeRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (user.getRole() != Role.ORGANIZATION) {
            throw new InvalidRoleException("This email is not associated with an organization");
        }

        authenticationService.sendVerificationCode(request.getEmail());
    }

    public User verifyCodeForOrganization(VerifyCodeRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (user.getRole() != Role.ORGANIZATION) {
            throw new InvalidRoleException("This email is not associated with an organization");
        }

        return authenticationService.authenticateWithCode(request.getEmail(), request.getCode());
    }
}
