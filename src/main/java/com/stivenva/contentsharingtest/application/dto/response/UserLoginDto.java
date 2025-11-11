package com.stivenva.contentsharingtest.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    public long id;
    public String email;
    public String firstName;
    public String lastName;
    public String username;
    public int ratingCount;
    public LocalDateTime createdAt;
}
