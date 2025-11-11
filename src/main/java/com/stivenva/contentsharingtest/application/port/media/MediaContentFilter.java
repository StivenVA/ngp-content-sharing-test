package com.stivenva.contentsharingtest.application.port.media;

import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.PageResult;

public interface MediaContentFilter {
    PageResult<MediaContent> filter(FilterMediaContentDto filterMediaContentDto);
}
