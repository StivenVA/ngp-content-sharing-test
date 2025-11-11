package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model;

import com.stivenva.contentsharingtest.domain.model.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "media_content")
public class MediaContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_content_id", nullable = false)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity userId;

    @Column(name = "media_content_title", nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "media_content_thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "media_content_content_url", nullable = false)
    private String contentUrl;

    @Column(name = "media_content_created_at", nullable = false)
    private LocalDateTime createdAt;

}
