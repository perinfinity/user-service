package com.perinfinity.auth_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class CookieServiceTest {

    private CookieService cookieService;

    @BeforeEach
    void setUp() {
        cookieService = new CookieService();
        ReflectionTestUtils.setField(cookieService, "secure", false);
        ReflectionTestUtils.setField(cookieService, "jwtExpirationMs", 3600000L);
    }

    @Test
    void createAuthCookie_setsCorrectAttributes() {
        ResponseCookie cookie = cookieService.createAuthCookie("test-jwt");

        assertThat(cookie.getName()).isEqualTo(CookieService.COOKIE_NAME);
        assertThat(cookie.getValue()).isEqualTo("test-jwt");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(3600L);
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    void clearAuthCookie_setsMaxAgeZeroAndEmptyValue() {
        ResponseCookie cookie = cookieService.clearAuthCookie();

        assertThat(cookie.getName()).isEqualTo(CookieService.COOKIE_NAME);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge().getSeconds()).isZero();
    }
}
