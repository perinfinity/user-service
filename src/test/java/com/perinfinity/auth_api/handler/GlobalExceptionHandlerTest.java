package com.perinfinity.auth_api.handler;

import com.perinfinity.auth_api.exceptions.EmailAlreadyUsedException;
import com.perinfinity.auth_api.exceptions.InvalidVerificationCodeException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn404_forUserNotFoundException() throws Exception {
        mockMvc.perform(get("/test-ex/user-not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn409_forEmailAlreadyUsedException() throws Exception {
        mockMvc.perform(get("/test-ex/email-conflict").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void shouldReturn401_forInvalidVerificationCodeException() throws Exception {
        mockMvc.perform(get("/test-ex/invalid-code").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldReturn401_forBadCredentialsException() throws Exception {
        mockMvc.perform(get("/test-ex/bad-credentials").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldReturn403_forDisabledException() throws Exception {
        mockMvc.perform(get("/test-ex/disabled").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldReturn403_forLockedException() throws Exception {
        mockMvc.perform(get("/test-ex/locked").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldReturn500_forGenericException() throws Exception {
        mockMvc.perform(get("/test-ex/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An internal error occurred"));
    }

    @RestController
    @RequestMapping("/test-ex")
    static class ThrowingController {

        @GetMapping("/user-not-found")
        void userNotFound() {
            throw new UserNotFoundException("test@example.com");
        }

        @GetMapping("/email-conflict")
        void emailConflict() {
            throw new EmailAlreadyUsedException("existing@example.com");
        }

        @GetMapping("/invalid-code")
        void invalidCode() {
            throw new InvalidVerificationCodeException();
        }

        @GetMapping("/bad-credentials")
        void badCredentials() {
            throw new BadCredentialsException("bad credentials");
        }

        @GetMapping("/disabled")
        void disabled() {
            throw new DisabledException("account disabled");
        }

        @GetMapping("/locked")
        void locked() {
            throw new LockedException("account locked");
        }

        @GetMapping("/generic")
        void generic() {
            throw new RuntimeException("unexpected error");
        }
    }
}
