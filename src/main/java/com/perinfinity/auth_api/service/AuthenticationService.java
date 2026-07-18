package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.LoginUserDto;
import com.perinfinity.auth_api.dtos.RegisterUserDto;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.EmailAlreadyUsedException;
import com.perinfinity.auth_api.exceptions.InvalidVerificationCodeException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationCodeService verificationCodeService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    public User signup(RegisterUserDto registerUserDto) {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new EmailAlreadyUsedException(registerUserDto.getEmail());
        }

        User user = User.builder()
                .username(registerUserDto.getUsername() != null ?
                    registerUserDto.getUsername() :
                    registerUserDto.getEmail().split("@")[0])
                .email(registerUserDto.getEmail())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .role("VOLUNTEER".equalsIgnoreCase(registerUserDto.getRole()) ?
                    Role.VOLUNTEER : Role.ORGANIZATION)
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .orgName(registerUserDto.getOrgName())
                .bio(registerUserDto.getBio())
                .address(registerUserDto.getAddress())
                .phone(registerUserDto.getPhone())
                .country(registerUserDto.getCountry())
                .city(registerUserDto.getCity())
                .profileImage(registerUserDto.getProfileImage())
                .preferredCategories(registerUserDto.getPreferredCategories())
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
                .orElseThrow(() -> new UserNotFoundException(input.getEmail()));
    }

    public void sendVerificationCode(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        String code = verificationCodeService.generateAndStoreCode(email);
        try {
            emailService.sendVerificationCode(email, code);
        } catch (Exception e) {
            log.warn("Email delivery failed for {} — code still valid (check server logs in dev): {}", email, e.getMessage());
        }
    }

    public User authenticateWithCode(String email, String code) {
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new InvalidVerificationCodeException();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void resetPassword(String email, String code, String newPassword) {
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new InvalidVerificationCodeException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
