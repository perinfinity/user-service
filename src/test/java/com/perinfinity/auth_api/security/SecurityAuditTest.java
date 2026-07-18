package com.perinfinity.auth_api.security;

import com.perinfinity.auth_api.filter.AuthenticationFilter;
import com.perinfinity.auth_api.service.CookieService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T-017 — Security audit tests.
 *
 * Verifies the critical security properties of the cookie-based JWT mechanism:
 *  - httpOnly flag (JS cannot read the token)
 *  - SameSite=Strict (mitigates CSRF)
 *  - No token stored in localStorage (enforced by absence of token in DTOs — tested via AuthenticationControllerTest)
 *  - Filter ignores requests with no credential
 *  - Authorization header takes priority over cookie (for API clients)
 */
class SecurityAuditTest {

    // ─── CookieService security properties ───────────────────────────────────

    @Test
    void authCookie_mustBeHttpOnly() {
        CookieService svc = cookieService();
        ResponseCookie cookie = svc.createAuthCookie("any-token");
        assertThat(cookie.isHttpOnly())
                .as("auth_token cookie MUST be httpOnly so JS cannot read it")
                .isTrue();
    }

    @Test
    void authCookie_mustHaveSameSiteStrict() {
        CookieService svc = cookieService();
        ResponseCookie cookie = svc.createAuthCookie("any-token");
        assertThat(cookie.getSameSite())
                .as("auth_token cookie MUST use SameSite=Strict to mitigate CSRF")
                .isEqualTo("Strict");
    }

    @Test
    void authCookie_secureFlagRespectedFromConfig() {
        CookieService svc = cookieService();
        ReflectionTestUtils.setField(svc, "secure", true);
        ResponseCookie cookie = svc.createAuthCookie("any-token");
        assertThat(cookie.isSecure())
                .as("Secure flag must be set when app.cookie.secure=true (production)")
                .isTrue();
    }

    @Test
    void clearCookie_maxAgeZero_preventsTokenReuse() {
        CookieService svc = cookieService();
        ResponseCookie cookie = svc.clearAuthCookie();
        assertThat(cookie.getMaxAge().getSeconds())
                .as("Logout cookie must expire immediately (MaxAge=0)")
                .isZero();
        assertThat(cookie.getValue())
                .as("Logout cookie value must be empty")
                .isEmpty();
        assertThat(cookie.isHttpOnly())
                .as("Logout cookie must also be httpOnly")
                .isTrue();
    }

    // ─── AuthenticationFilter token extraction ────────────────────────────────

    @Test
    void filter_returnsNull_whenNoCookieAndNoHeader() {
        AuthenticationFilter filter = new AuthenticationFilter(null, null, null);
        MockHttpServletRequest req = new MockHttpServletRequest();

        assertThat(filter.extractToken(req))
                .as("No credential → no token extracted (unauthenticated request passes through)")
                .isNull();
    }

    @Test
    void filter_extractsToken_fromAuthorizationHeader() {
        AuthenticationFilter filter = new AuthenticationFilter(null, null, null);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer header-jwt");

        assertThat(filter.extractToken(req)).isEqualTo("header-jwt");
    }

    @Test
    void filter_extractsToken_fromCookie_whenNoHeader() {
        AuthenticationFilter filter = new AuthenticationFilter(null, null, null);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie(CookieService.COOKIE_NAME, "cookie-jwt"));

        assertThat(filter.extractToken(req)).isEqualTo("cookie-jwt");
    }

    @Test
    void filter_prefersAuthorizationHeader_overCookie() {
        AuthenticationFilter filter = new AuthenticationFilter(null, null, null);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer header-jwt");
        req.setCookies(new Cookie(CookieService.COOKIE_NAME, "cookie-jwt"));

        assertThat(filter.extractToken(req))
                .as("Authorization header must take priority over cookie for API client compatibility")
                .isEqualTo("header-jwt");
    }

    @Test
    void filter_ignoresMalformedAuthorizationHeader() {
        AuthenticationFilter filter = new AuthenticationFilter(null, null, null);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Basic dXNlcjpwYXNz"); // Basic auth — not Bearer

        assertThat(filter.extractToken(req))
                .as("Non-Bearer Authorization header must be ignored")
                .isNull();
    }

    // ─── Cookie name consistency ───────────────────────────────────────────────

    @Test
    void cookieName_isConsistentBetweenServiceAndFilter() {
        // The filter reads by CookieService.COOKIE_NAME — both must agree on the name
        assertThat(CookieService.COOKIE_NAME).isEqualTo("auth_token");
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private CookieService cookieService() {
        CookieService svc = new CookieService();
        ReflectionTestUtils.setField(svc, "secure", false);
        ReflectionTestUtils.setField(svc, "jwtExpirationMs", 3600000L);
        return svc;
    }
}
