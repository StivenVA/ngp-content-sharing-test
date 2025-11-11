package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.request.EditRateRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RateRequest;
import com.stivenva.contentsharingtest.application.port.rating.RatingService;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.Rating;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.RatingRepository;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MediaContentRepository mediaContentRepository;

    @Transactional
    @Override
    public void rate(RateRequest rateRequest) {

        double mediaRating = rateRequest.stars;

        if(mediaRating < 1 || mediaRating > 5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User userRating = userRepository.findByUsername(rateRequest.username)
                .orElseThrow(() -> new RuntimeException("User not found: " + rateRequest.username));

        MediaContent mediaContentToRate = mediaContentRepository.findById(rateRequest.mediaContentId)
                .orElseThrow(() -> new RuntimeException("MediaContent not found with id: " + rateRequest.mediaContentId));

        Rating rating = new Rating(
                null,
                userRating.id(),
                mediaContentToRate.id(),
                mediaRating,
                rateRequest.comment,
                LocalDateTime.now()
        );

        ratingRepository.save(rating);
        userRepository.updateRateCount(userRating.id());
    }

    @Override
    public void editRate(EditRateRequestDto editRateRequestDto) {

        Rating existingRating = getRatingFromUserAndMediaContent(editRateRequestDto.username, editRateRequestDto.mediaContentId);
        Double newRatingValue = editRateRequestDto.rating;

        if(newRatingValue < 1 || newRatingValue > 5){
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Rating updatedRating = new Rating(
                existingRating.id(),
                existingRating.userId(),
                existingRating.mediaContentId(),
                newRatingValue == null ? existingRating.stars() : newRatingValue,
                editRateRequestDto.comment == null ? existingRating.comment() : editRateRequestDto.comment,
                existingRating.createdAt()
        );

        ratingRepository.save(updatedRating);

    }

    @Override
    public List<Rating> getRatingsFromMediaContent(Long mediaContentId) {

        if(mediaContentId == null){
            throw new IllegalArgumentException("mediaContentId is required");
        }

        return (List<Rating>) ratingRepository.findAllByMediaContentId(mediaContentId);
    }

    @Override
    public Rating getRatingFromUserAndMediaContent(String username, Long mediaContentId) {

        if(username == null || mediaContentId == null){
            throw new IllegalArgumentException("username and mediaContentId are required");
        }

        return ratingRepository.findByUserIdAndMediaContentId(userRepository.findByUsername(username).orElseThrow().id(), mediaContentId)
                .orElseThrow(()-> new RuntimeException("Rating not found for user: " + username + " and mediaContentId: " + mediaContentId));
    }

    @Override
    public void deleteRating(String username, Long mediaContentId) {

        Rating ratingToDelete = getRatingFromUserAndMediaContent(username, mediaContentId);
        ratingRepository.delete(ratingToDelete);

    }
}
