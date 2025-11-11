package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.adapter;

import com.stivenva.contentsharingtest.domain.model.Rating;
import com.stivenva.contentsharingtest.infrastructure.mapper.RatingMapper;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.RatingEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryJpaAdapter implements com.stivenva.contentsharingtest.domain.port.repository.RatingRepository{

    private final RatingRepository ratingRepository;

    @Override
    public void save(Rating rating) {

        RatingEntity ratingEntity = RatingMapper.toEntity(rating);
        ratingRepository.save(ratingEntity);
    }

    @Override
    public void delete(Rating rating) {

        ratingRepository.delete(RatingMapper.toEntity(rating));
    }

    @Override
    public void deleteById(long id) {
        ratingRepository.deleteById(id);
    }

    @Override
    public Optional<Rating> findByUserIdAndMediaContentId(long userId, long mediaContentId) {
        return ratingRepository.findByUserIdAndMediaContentId(userId, mediaContentId)
                .map(RatingMapper::toDomain);
    }

    @Override
    public Optional<Rating> findById(long id) {
        return ratingRepository.findById(id)
                .map(RatingMapper::toDomain);
    }

    @Override
    public Iterable<Rating> findAll() {
        return null;
    }

    @Override
    public Iterable<Rating> findAllByMediaContentId(long mediaContentId) {
        return ratingRepository.findAllByMediaContentId(mediaContentId)
                .stream()
                .map(RatingMapper::toDomain)
                .toList();
    }
}
