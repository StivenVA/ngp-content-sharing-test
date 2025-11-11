package com.stivenva.contentsharingtest.domain.port.repository;

import com.stivenva.contentsharingtest.domain.model.Rating;

import java.util.Optional;

public interface RatingRepository {

    void save(Rating rating);
    void delete(Rating rating);
    void deleteById(long id);
    Optional<Rating> findByUserIdAndMediaContentId(long userId, long mediaContentId);
    Optional<Rating> findById(long id);
    Iterable<Rating> findAll();

    Iterable<Rating> findAllByMediaContentId(long mediaContentId);
}
