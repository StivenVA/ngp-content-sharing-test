package com.stivenva.contentsharingtest.application.port.media;

import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentResponseDto;
import org.springframework.data.domain.Page;

public interface MediaContentFilter {
    Page<MediaContentResponseDto> filter(FilterMediaContentDto filterMediaContentDto);

    MediaContentResponseDto findById(long id);

    Page<MediaContentResponseDto> findAll(int page, int size);

    Page<MediaContentResponseDto> findByUserId(long userId, int page, int size);
}
