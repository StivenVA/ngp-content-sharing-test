package com.stivenva.contentsharingtest.infrastructure.security;

import com.stivenva.contentsharingtest.application.service.AuthServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;

import java.io.IOException;
import java.util.List;

public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final CognitoIdentityProviderClient cognitoClient;

    public JwtCookieAuthenticationFilter(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path == null || !path.startsWith("/api/auth/")) {

            String accessToken = extractAccessTokenFromCookies(request.getCookies());
            if (accessToken != null && !accessToken.isBlank()) {
                try {
                    GetUserResponse userResponse = cognitoClient.getUser(GetUserRequest.builder().accessToken(accessToken).build());
                    if (userResponse != null && userResponse.username() != null) {
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userResponse.username(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (NotAuthorizedException ex) {
                } catch (Exception e) {
                }
            }
        }

        filterChain.doFilter(request, response);
    }
    private String extractAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (AuthServiceImpl.ACCESS_TOKEN_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
