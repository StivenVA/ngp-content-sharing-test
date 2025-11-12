package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.request.EditRateRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RateRequest;
import com.stivenva.contentsharingtest.application.dto.response.MediaRatingDto;
import com.stivenva.contentsharingtest.application.port.rating.RatingService;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.Rating;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.RatingRepository;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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

    @PreAuthorize("@resourceAuthorizationService.isRatingOwner(#editRateRequestDto.username, #editRateRequestDto.ratingId)")
    @Override
    public void editRate(EditRateRequestDto editRateRequestDto) {

        Rating existingRating = ratingRepository.findById(editRateRequestDto.ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + editRateRequestDto.ratingId));
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
    public List<MediaRatingDto> getRatingsFromMediaContent(Long mediaContentId) {

        if(mediaContentId == null){
            throw new IllegalArgumentException("mediaContentId is required");
        }

        Iterable<Rating> ratingIterable = ratingRepository.findAllByMediaContentId(mediaContentId);

        List<MediaRatingDto> ratings = new ArrayList<>();

        for(Rating rating : ratingIterable){
            MediaRatingDto mediaRatingDto = new MediaRatingDto();

            mediaRatingDto.stars = rating.stars();
            mediaRatingDto.comment = rating.comment();
            mediaRatingDto.userId = rating.userId();

            User user = userRepository.findById(rating.userId()).orElseThrow();
            mediaRatingDto.userFirstName = user.firstname();
            mediaRatingDto.userLastName = user.lastname();

            ratings.add(mediaRatingDto);
        }

        return ratings;
    }

    @Override
    public Rating getRatingFromUserAndMediaContent(String username, Long mediaContentId) {

        if(username == null || mediaContentId == null){
            throw new IllegalArgumentException("username and mediaContentId are required");
        }

        return ratingRepository.findByUserIdAndMediaContentId(userRepository.findByUsername(username).orElseThrow().id(), mediaContentId)
                .orElseThrow(()-> new RuntimeException("Rating not found for user: " + username + " and mediaContentId: " + mediaContentId));
    }

    @PreAuthorize("@resourceAuthorizationService.isRatingOwner(#username, #ratingId)")
    @Override
    public void deleteRating(String username, Long ratingId) {
        Rating ratingToDelete = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + ratingId));

        ratingRepository.delete(ratingToDelete);
    }
}
