package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository;

import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<RatingEntity,Long> {

    @Query("SELECT r FROM RatingEntity r WHERE r.userId.id = ?1 and r.mediaContentId.id =?2")
    Optional<RatingEntity> findByUserIdAndMediaContentId(long userId, long mediaContentId);

    @Query("SELECT r FROM RatingEntity r WHERE r.mediaContentId.id =?1")
    List<RatingEntity> findAllByMediaContentId(long mediaContentId);
}
