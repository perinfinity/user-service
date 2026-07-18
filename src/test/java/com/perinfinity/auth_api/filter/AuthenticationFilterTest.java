package com.perinfinity.auth_api.filter;

import com.perinfinity.auth_api.service.CookieService;
import com.perinfinity.auth_api.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    private AuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationFilter(jwtService, userDetailsService, handlerExceptionResolver);
    }

    @Test
    void extractToken_returnsNull_whenNeitherHeaderNorCookiePresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = filter.extractToken(request);

        assertThat(token).isNull();
    }

    @Test
    void extractToken_readsFromAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer header-token");

        String token = filter.extractToken(request);

        assertThat(token).isEqualTo("header-token");
    }

    @Test
    void extractToken_readsFromCookie_whenNoHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(CookieService.COOKIE_NAME, "cookie-token"));

        String token = filter.extractToken(request);

        assertThat(token).isEqualTo("cookie-token");
    }

    @Test
    void extractToken_prefersAuthorizationHeader_overCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer header-token");
        request.setCookies(new Cookie(CookieService.COOKIE_NAME, "cookie-token"));

        String token = filter.extractToken(request);

        assertThat(token).isEqualTo("header-token");
    }

    @Test
    void extractToken_ignoresUnrelatedCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("session_id", "abc123"), new Cookie("theme", "dark"));

        String token = filter.extractToken(request);

        assertThat(token).isNull();
    }
}
