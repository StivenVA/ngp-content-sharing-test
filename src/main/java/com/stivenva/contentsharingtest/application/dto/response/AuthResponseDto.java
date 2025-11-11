package com.stivenva.contentsharingtest.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AuthResponseDto{

    public UserLoginDto user;
    public String accessToken;
    public String refreshToken;
    public long expiresIn;
    public long refreshExpiresIn;
}
