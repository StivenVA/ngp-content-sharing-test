package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long id;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "user_firstname", nullable = false)
    String firstname;

    @Column(name = "user_lastname", nullable = false)
    String lastname;

    @Column(name = "user_email", nullable = false)
    String email;

    @Column(name = "user_rating_count", nullable = false)
    int ratingCount;

    @Column(name = "user_last_login")
    LocalDateTime lastLogin;

    @Column(name = "user_created_at", nullable = false)
    LocalDateTime createdAt;
}
