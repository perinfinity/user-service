package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.LoginUserDto;
import com.perinfinity.auth_api.dtos.RegisterUserDto;
import com.perinfinity.auth_api.dtos.RegistrationResponseDto;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto registerUserDto) {
        User user = User
                .builder()
                .username(registerUserDto.getEmail().split("@")[0])
                .email(registerUserDto.getEmail())
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .role("VOLUNTEER".equalsIgnoreCase(registerUserDto.getRole())? User.Role.VOLUNTEER : User.Role.ORGANIZATION)
                .build();

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}