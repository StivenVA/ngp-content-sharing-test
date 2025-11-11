package com.stivenva.contentsharingtest.application.port.auth;

import com.stivenva.contentsharingtest.application.dto.request.AuthRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RegisterRequestDto;
import com.stivenva.contentsharingtest.application.dto.response.AuthResponseDto;

public interface AuthService {

    String ACCESS_TOKEN_COOKIE = "accessToken";
    String REFRESH_TOKEN_COOKIE = "refreshToken";

    AuthResponseDto login(AuthRequestDto authRequestDto);
    void register(RegisterRequestDto registerRequestDto);

    void confirmAccount(String email,String code);

    AuthResponseDto refreshToken(String refreshToken);

    void logout(String accessToken);
}
