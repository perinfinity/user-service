package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.SendCodeRequestDto;
import com.perinfinity.auth_api.dtos.VerifyCodeRequestDto;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.InvalidRoleException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganizationAuthServiceTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrganizationAuthService organizationAuthService;

    @Test
    void sendCodeForOrganization_shouldThrowUserNotFoundException_whenUserNotFound() {
        given(userRepository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

        SendCodeRequestDto request = SendCodeRequestDto.builder().email("unknown@test.com").build();

        assertThatThrownBy(() -> organizationAuthService.sendCodeForOrganization(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void sendCodeForOrganization_shouldThrowInvalidRoleException_whenUserIsNotOrganization() {
        User volunteer = User.builder().email("vol@test.com").role(Role.VOLUNTEER).build();
        given(userRepository.findByEmail("vol@test.com")).willReturn(Optional.of(volunteer));

        SendCodeRequestDto request = SendCodeRequestDto.builder().email("vol@test.com").build();

        assertThatThrownBy(() -> organizationAuthService.sendCodeForOrganization(request))
                .isInstanceOf(InvalidRoleException.class);
    }

    @Test
    void verifyCodeForOrganization_shouldThrowUserNotFoundException_whenUserNotFound() {
        given(userRepository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

        VerifyCodeRequestDto request = new VerifyCodeRequestDto();
        request.setEmail("unknown@test.com");
        request.setCode("123456");

        assertThatThrownBy(() -> organizationAuthService.verifyCodeForOrganization(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void verifyCodeForOrganization_shouldThrowInvalidRoleException_whenUserIsNotOrganization() {
        User volunteer = User.builder().email("vol@test.com").role(Role.VOLUNTEER).build();
        given(userRepository.findByEmail("vol@test.com")).willReturn(Optional.of(volunteer));

        VerifyCodeRequestDto request = new VerifyCodeRequestDto();
        request.setEmail("vol@test.com");
        request.setCode("123456");

        assertThatThrownBy(() -> organizationAuthService.verifyCodeForOrganization(request))
                .isInstanceOf(InvalidRoleException.class);
    }
}
