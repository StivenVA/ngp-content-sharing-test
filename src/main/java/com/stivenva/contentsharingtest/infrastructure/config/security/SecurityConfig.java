package com.stivenva.contentsharingtest.infrastructure.config.security;

import com.stivenva.contentsharingtest.application.port.auth.AuthService;
import jakarta.servlet.http.Cookie; // Import Cookie
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/auth/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")

                        .addLogoutHandler((request, response, auth) -> {

                            String accessToken = extractAccessTokenFromCookies(request.getCookies());
                            if (accessToken != null) {
                                authService.logout(accessToken);
                            }
                        })
                        .deleteCookies(AuthService.ACCESS_TOKEN_COOKIE, AuthService.REFRESH_TOKEN_COOKIE)
                        .logoutSuccessHandler((request,
                                               response,
                                               auth) ->
                                response.setStatus(200)
                        )
                ).exceptionHandling(exception -> {

                            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
                            exception.accessDeniedHandler(customAccessDeniedHandler);
                        }
                );

        return http.build();
    }

    private String extractAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (AuthService.ACCESS_TOKEN_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}