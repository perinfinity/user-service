package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.LoginUserDto;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.EmailAlreadyUsedException;
import com.perinfinity.auth_api.exceptions.InvalidVerificationCodeException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private VerificationCodeService verificationCodeService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_shouldThrowUserNotFoundException_whenUserNotInRepository() {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("ghost@test.com");
        dto.setPassword("pass");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);
        given(userRepository.findByEmail("ghost@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(dto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void sendVerificationCode_shouldThrowUserNotFoundException_whenUserNotFound() {
        given(userRepository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.sendVerificationCode("unknown@test.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void authenticateWithCode_shouldThrowInvalidVerificationCodeException_whenCodeIsInvalid() {
        given(verificationCodeService.verifyCode("test@test.com", "000000")).willReturn(false);

        assertThatThrownBy(() -> authenticationService.authenticateWithCode("test@test.com", "000000"))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

    @Test
    void resetPassword_shouldThrowInvalidVerificationCodeException_whenCodeIsInvalid() {
        given(verificationCodeService.verifyCode("test@test.com", "wrong")).willReturn(false);

        assertThatThrownBy(() -> authenticationService.resetPassword("test@test.com", "wrong", "newpass"))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

    @Test
    void resetPassword_shouldThrowUserNotFoundException_whenCodeValidButUserMissing() {
        given(verificationCodeService.verifyCode("test@test.com", "123456")).willReturn(true);
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.resetPassword("test@test.com", "123456", "newpass"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void signup_shouldThrowEmailAlreadyUsedException_whenEmailExists() {
        com.perinfinity.auth_api.dtos.RegisterUserDto dto = new com.perinfinity.auth_api.dtos.RegisterUserDto();
        dto.setEmail("exists@test.com");
        given(userRepository.existsByEmail("exists@test.com")).willReturn(true);

        assertThatThrownBy(() -> authenticationService.signup(dto))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void signup_shouldMapProfileFields_whenProvided() {
        com.perinfinity.auth_api.dtos.RegisterUserDto dto = new com.perinfinity.auth_api.dtos.RegisterUserDto();
        dto.setEmail("new@test.com");
        dto.setPassword("pass");
        dto.setCountry("CM");
        dto.setCity("Douala");
        dto.setProfileImage("https://cdn.example.com/avatar.jpg");
        dto.setPreferredCategories(List.of("Environnement", "Éducation"));

        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(passwordEncoder.encode("pass")).willReturn("encoded");
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        User saved = authenticationService.signup(dto);

        assertThat(saved.getCountry()).isEqualTo("CM");
        assertThat(saved.getCity()).isEqualTo("Douala");
        assertThat(saved.getProfileImage()).isEqualTo("https://cdn.example.com/avatar.jpg");
        assertThat(saved.getPreferredCategories()).containsExactly("Environnement", "Éducation");
    }
}
