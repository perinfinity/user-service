package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private final User user = User.builder()
            .id(2L)
            .username("aline_vol")
            .email("aline@example.com")
            .password("hashed")
            .role(Role.VOLUNTEER)
            .build();

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String key = Base64.getEncoder().encodeToString("test_secret_key_for_testing_only_32b".getBytes());
        ReflectionTestUtils.setField(jwtService, "secretKey", key);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void token_subject_isTheUserEmail() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.extractUsername(token)).isEqualTo("aline@example.com");
    }

    /**
     * Régression : le sujet du token est l'email, mais User.getUsername() renvoie
     * le pseudo. isTokenValid doit comparer le sujet à l'email, sinon toute session
     * d'un utilisateur dont le pseudo diffère de l'email est rejetée (bug /me 500).
     */
    @Test
    void isTokenValid_isTrue_whenUsernameDiffersFromEmail() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_isFalse_forAnotherUser() {
        String token = jwtService.generateToken(user);
        User other = User.builder()
                .id(3L)
                .username("bob")
                .email("bob@example.com")
                .password("hashed")
                .role(Role.VOLUNTEER)
                .build();
        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    void token_carries_userId_and_role_claims() {
        String token = jwtService.generateToken(user);
        Number userId = jwtService.extractClaim(token, c -> c.get("userId", Number.class));
        String role = jwtService.extractClaim(token, c -> c.get("role", String.class));
        assertThat(userId.longValue()).isEqualTo(2L);
        assertThat(role).isEqualTo("VOLUNTEER");
    }
}
