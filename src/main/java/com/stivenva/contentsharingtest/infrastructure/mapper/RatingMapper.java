package com.stivenva.contentsharingtest.infrastructure.mapper;

import com.stivenva.contentsharingtest.domain.model.Rating;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.MediaContentEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.RatingEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.UserEntity;

public final class RatingMapper {

    public static Rating toDomain(RatingEntity e){
        return new Rating(
                e.getId(),
                e.getUserId().getId(),
                e.getMediaContentId().getId(),
                e.getStars(),
                e.getComment(),
                e.getCreatedAt()
        );
    }

    public static RatingEntity toEntity(Rating d){
        RatingEntity ratingEntity = new RatingEntity();

        ratingEntity.setStars(d.stars());
        ratingEntity.setComment(d.comment());
        ratingEntity.setCreatedAt(d.createdAt());
        ratingEntity.setId(d.id());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(d.userId());

        ratingEntity.setUserId(userEntity);

        MediaContentEntity mediaContentEntity = new MediaContentEntity();
        mediaContentEntity.setId(d.mediaContentId());

        ratingEntity.setMediaContentId(mediaContentEntity);

        return ratingEntity;
    }
}
