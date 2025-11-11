package com.stivenva.contentsharingtest.domain.port;


import java.util.Optional;

public interface IdentityProvider {

    Optional<?> login(String username, String password);
    Optional<?> register(String username, String email, String password);

    void confirmAccount(String email,String code);

    boolean isEmailAvailable(String email);

    Optional<?> refreshToken(String refreshToken);

    void logout(String accessToken);
}
