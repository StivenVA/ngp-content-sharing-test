package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.port.media.MediaContentFilter;
import com.stivenva.contentsharingtest.domain.model.Category;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.PageResult;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import org.springframework.stereotype.Service;

@Service
public class MediaContentFilterService implements MediaContentFilter {

    private final MediaContentRepository repository;

    public MediaContentFilterService(MediaContentRepository repository) {
        this.repository = repository;
    }

    public PageResult<MediaContent> filter(FilterMediaContentDto filterMediaContentDto) {

        Category category;

        try {
            category = Category.valueOf(filterMediaContentDto.category);
        }catch (IllegalArgumentException ex) {
            return null;
        }

        return repository.filter(
                filterMediaContentDto.title,
                filterMediaContentDto.description,
                category,
                filterMediaContentDto.page,
                filterMediaContentDto.size
        );
    }
}
