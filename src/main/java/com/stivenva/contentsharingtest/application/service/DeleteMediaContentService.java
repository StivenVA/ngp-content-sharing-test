package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.port.media.DeleteMediaContent;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.Rating;
import com.stivenva.contentsharingtest.domain.port.StorageService;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import static com.stivenva.contentsharingtest.application.util.S3UrlKeyExtractor.extractKeyFromUrl;

@Service
@RequiredArgsConstructor
public class DeleteMediaContentService implements DeleteMediaContent {

    private final MediaContentRepository mediaContentRepository;
    private final StorageService storageService;
    private final RatingRepository ratingRepository;

    @Transactional
    @Override
    public void delete(long mediaContentId,String username) {

        MediaContent content = mediaContentRepository.findById(mediaContentId)
                .orElseThrow(() -> new RuntimeException("MediaContent not found with id: " + mediaContentId));

        deleteRatingBeforeMedia(mediaContentId);

        String mediaKey = extractKeyFromUrl(content.contentUrl());
        String thumbnailKey = extractKeyFromUrl(content.thumbnailUrl());

        storageService.delete(mediaKey);
        storageService.delete(thumbnailKey);

        mediaContentRepository.delete(content);
    }

    private void deleteRatingBeforeMedia(long mediaContentId){

        Iterable<Rating> ratings = ratingRepository.findAllByMediaContentId(mediaContentId);

        for(Rating rating : ratings){
            ratingRepository.delete(rating);
        }

    }
}
