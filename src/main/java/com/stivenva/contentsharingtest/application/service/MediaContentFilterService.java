package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentResponseDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaRatingDto;
import com.stivenva.contentsharingtest.application.port.media.MediaContentFilter;
import com.stivenva.contentsharingtest.application.port.rating.RatingService;
import com.stivenva.contentsharingtest.domain.model.Category;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaContentFilterService implements MediaContentFilter {

    private final MediaContentRepository repository;
    private final RatingService ratingService;

    @Override
    public Page<MediaContentResponseDto> filter(FilterMediaContentDto filterMediaContentDto) {

        Category category;

        try {
            category = Category.valueOf(filterMediaContentDto.category);
        }catch (IllegalArgumentException ex) {
            return null;
        }

        Page<MediaContent> mediaContentPagedResult = repository.filter(
                filterMediaContentDto.title,
                filterMediaContentDto.description,
                category,
                filterMediaContentDto.page,
                filterMediaContentDto.size
        );

        return mapToResponseDto(mediaContentPagedResult);
    }

    @Override
    public MediaContentResponseDto findById(long id) {

        MediaContent mediaContent = repository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Media content with id " + id + " not found"));

        return mapToResponseDto(mediaContent);
    }

    @Override
    public Page<MediaContentResponseDto> findAll(int page, int size) {

        List<MediaContent> allMediaContent = repository.findAll();

        return createPageableList(allMediaContent,page,size);
    }

    @Override
    public Page<MediaContentResponseDto> findByUserId(long userId, int page, int size) {

        List<MediaContent> userMediaContentList = repository.findByUserId(userId);

        return createPageableList(userMediaContentList,page,size);
    }

    private Page<MediaContentResponseDto> createPageableList(List<MediaContent> mediaContentList, int page,int size){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<MediaContent> allMediaContentPaged = new PageImpl<>(mediaContentList,pageable,mediaContentList.size());

        return mapToResponseDto(allMediaContentPaged);
    }

    private Page<MediaContentResponseDto> mapToResponseDto(Page<MediaContent> mediaContentPagedResult) {

        if(!mediaContentPagedResult.hasContent()){
            return Page.empty();
        }

        return mediaContentPagedResult
                .map(mediaContent -> {
                    MediaContentResponseDto mediaContentResponseDto = mapToResponseDto(mediaContent);

                    mediaContentResponseDto.ratings = ratingService.getRatingsFromMediaContent(mediaContent.id());

                    return mediaContentResponseDto;
                });

    }

    private MediaContentResponseDto mapToResponseDto(MediaContent mediaContent) {
        MediaContentResponseDto mediaContentResponseDto = new MediaContentResponseDto();

        mediaContentResponseDto.id = mediaContent.id();
        mediaContentResponseDto.mediaUrl = mediaContent.contentUrl();
        mediaContentResponseDto.title = mediaContent.title();
        mediaContentResponseDto.description = mediaContent.description();
        mediaContentResponseDto.category = mediaContent.category().name();
        mediaContentResponseDto.thumbnailUrl = mediaContent.thumbnailUrl();

        return mediaContentResponseDto;
    }
}
