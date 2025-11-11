package com.stivenva.contentsharingtest.infrastructure.mapper;

import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.MediaContentEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.UserEntity;

public final class MediaContentMapper {

    public static MediaContent toDomain(MediaContentEntity e) {
        return new MediaContent(
                e.getId() == null ? 0L : e.getId(),
                e.getUserId() == null ? 0L : e.getUserId().getId(),
                e.getTitle(),
                e.getDescription(),
                e.getCategory(),
                e.getThumbnailUrl(),
                e.getContentUrl(),
                e.getCreatedAt()
        );
    }

    public static MediaContentEntity toEntity(MediaContent d) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(d.userId());

        MediaContentEntity e = new MediaContentEntity();
        e.setId(d.id() == 0 ? null : d.id());
        e.setUserId(userEntity);
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setCategory(d.category());
        e.setThumbnailUrl(d.thumbnailUrl());
        e.setContentUrl(d.contentUrl());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
