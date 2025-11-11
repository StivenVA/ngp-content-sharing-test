package com.stivenva.contentsharingtest.application.port.rating;

import com.stivenva.contentsharingtest.application.dto.request.EditRateRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RateRequest;
import com.stivenva.contentsharingtest.application.dto.response.MediaRatingDto;
import com.stivenva.contentsharingtest.domain.model.Rating;

import java.util.List;

public interface RatingService {

    void rate(RateRequest rateRequest);
    void editRate(EditRateRequestDto editRateRequestDto);
    List<MediaRatingDto> getRatingsFromMediaContent(Long mediaContentId);
    Rating getRatingFromUserAndMediaContent(String username, Long mediaContentId);
    void deleteRating(String username, Long ratingId);
}
