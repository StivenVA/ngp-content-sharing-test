package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.request.AuthRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RegisterRequestDto;
import com.stivenva.contentsharingtest.application.dto.response.AuthResponseDto;
import com.stivenva.contentsharingtest.application.dto.response.UserLoginDto;
import com.stivenva.contentsharingtest.application.port.auth.AuthService;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.IdentityProvider;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {



    private final IdentityProvider identityProvider;
    private final UserRepository userRepository;

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        Optional<?> loginResponse = identityProvider.login(authRequestDto.email, authRequestDto.password);
        if (loginResponse.isEmpty() || !(loginResponse.get() instanceof AuthResponseDto authResponse)) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByEmail(authRequestDto.email)
                .orElseThrow(() -> new RuntimeException("User not found in the database"));

        user = user.updateLastLogin(LocalDateTime.now());
        userRepository.save(user);

        authResponse.user = new UserLoginDto(
                user.id(),
                user.email(),
                user.firstname(),
                user.lastname(),
                user.username(),
                user.ratingCount(),
                user.createdAt()
        );

        return authResponse;
    }

    @Override
    public void register(RegisterRequestDto registerRequestDto) {

        Optional<?> registrationResult = identityProvider.register(registerRequestDto.email, registerRequestDto.email, registerRequestDto.password);

        String username;

        if(registrationResult.isPresent() && ( registrationResult.get() instanceof SignUpResponse)){
            username = ((SignUpResponse) registrationResult.get()).userSub();
        }
        else{
            throw new RuntimeException("Registration failed");
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                0,
                username,
                registerRequestDto.firstname,
                registerRequestDto.lastname,
                registerRequestDto.email,
                0,
                null,
                now
        );
        userRepository.save(user);
    }

    @Override
    public void confirmAccount(String email, String code) {
        identityProvider.confirmAccount(email, code);
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken) {
        Optional<?> res = identityProvider.refreshToken(refreshToken);
        if (res.isEmpty() || !(res.get() instanceof AuthResponseDto authResponse)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = extractUsernameFromJwt(authResponse.accessToken);
        if (username != null && !username.isBlank()) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found in the database"));
            authResponse.user = new UserLoginDto(
                    user.id(),
                    user.email(),
                    user.firstname(),
                    user.lastname(),
                    user.username(),
                    user.ratingCount(),
                    user.createdAt()
            );
        }
        return authResponse;
    }

    @Override
    public void logout(String accessToken) {
        identityProvider.logout(accessToken);
    }

    private String extractUsernameFromJwt(String jwt) {
        try {
            if (jwt == null) return null;
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            byte[] payload = java.util.Base64.getUrlDecoder().decode(parts[1]);
            String json = new String(payload, java.nio.charset.StandardCharsets.UTF_8);
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            if (node.has("username")) return node.get("username").asText();
            if (node.has("email")) return node.get("email").asText();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
