package com.stivenva.contentsharingtest.domain.model;

import java.time.LocalDateTime;

public record Rating(
        Long id,
        long userId,
        long mediaContentId,
        double stars,
        String comment,
        LocalDateTime createdAt
) {
}
