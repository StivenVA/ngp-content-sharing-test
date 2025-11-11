package com.stivenva.contentsharingtest.domain.model;

import java.time.LocalDateTime;


public record MediaContent (
        long id,
        long userId,
        String title,
        String description,
        Category category,
        String thumbnailUrl,
        String contentUrl,
        LocalDateTime createdAt
)
{
}
