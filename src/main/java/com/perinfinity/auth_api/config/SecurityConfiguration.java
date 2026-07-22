package com.perinfinity.auth_api.config;

import com.perinfinity.auth_api.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationFilter authenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfiguration(AuthenticationFilter authenticationFilter,
                                  CorsConfigurationSource corsConfigurationSource) {
        this.authenticationFilter = authenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/send-code", "/api/auth/verify-code", "/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/v1/countries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/volunteers/by-id/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/volunteers/{username}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/volunteers/me").hasRole("VOLUNTEER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/volunteers/me").hasRole("VOLUNTEER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/organizations/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/organizations/me").hasRole("ORGANIZATION")
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html","/docs/**", "/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Endpoints internes service-à-service — protégés par clé partagée dans le contrôleur
                        .requestMatchers("/api/internal/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Allow H2 console frames in dev
        httpSecurity.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return httpSecurity.build();
    }
}
