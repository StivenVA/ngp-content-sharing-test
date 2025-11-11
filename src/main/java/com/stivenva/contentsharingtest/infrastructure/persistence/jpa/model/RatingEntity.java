package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "rating_id", nullable = false)
    private Long id;

    @JoinColumn(name = "media_content_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private MediaContentEntity mediaContentId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity userId;

    @Column(name = "rating_stars", nullable = false)
    private double stars;

    @Column(name = "rating_comment", length = 2000)
    private String comment;

    @Column(name = "rating_created_at", nullable = false)
    private LocalDateTime createdAt;
}
