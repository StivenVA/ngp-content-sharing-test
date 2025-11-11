package com.stivenva.contentsharingtest.infrastructure.web;

import com.stivenva.contentsharingtest.application.dto.request.AuthRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RegisterRequestDto;
import com.stivenva.contentsharingtest.application.dto.response.AuthResponseDto;
import com.stivenva.contentsharingtest.application.dto.response.UserLoginDto;
import com.stivenva.contentsharingtest.application.port.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse response){
        AuthResponseDto authResponse = authService.login(authRequestDto);

        Cookie accessTokenCookie = createCookie(
                AuthService.ACCESS_TOKEN_COOKIE,
                authResponse.accessToken,
                (int) authResponse.expiresIn
        );

        Cookie refreshTokenCookie = createCookie(
                AuthService.REFRESH_TOKEN_COOKIE,
                authResponse.refreshToken,
                (int) authResponse.refreshExpiresIn
        );

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(authResponse.user);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<UserLoginDto> refresh(jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response){
        String refreshToken = extractCookie(request.getCookies(), AuthService.REFRESH_TOKEN_COOKIE);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponseDto authResponse = authService.refreshToken(refreshToken);

        Cookie accessTokenCookie = createCookie(
                AuthService.ACCESS_TOKEN_COOKIE,
                authResponse.accessToken,
                (int) authResponse.expiresIn
        );
        Cookie refreshTokenCookie = createCookie(
                AuthService.REFRESH_TOKEN_COOKIE,
                authResponse.refreshToken,
                (int) authResponse.refreshExpiresIn
        );

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(authResponse.user);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String email, @RequestParam String code){

        try{
            authService.confirmAccount(email, code);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private Cookie createCookie(String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    private String extractCookie(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
