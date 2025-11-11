package com.stivenva.contentsharingtest.domain.model;

import java.time.LocalDateTime;


public record User(
        long id,
        String username,
        String firstname,
        String lastname,
        String email,
        int ratingCount,
        LocalDateTime lastLogin,
        LocalDateTime createdAt
) {

    public User updateLastLogin(LocalDateTime lastLogin) {
        return new User(
                this.id,
                this.username,
                this.firstname,
                this.lastname,
                this.email,
                this.ratingCount,
                lastLogin,
                this.createdAt
        );
    }
}
