package com.stivenva.contentsharingtest.application.port.media;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.PageResult;
import org.springframework.web.multipart.MultipartFile;

public interface MediaContentService {
    PageResult<MediaContent> filter(FilterMediaContentDto filterMediaContentDto);
    MediaContentCreatedDto create(MultipartFile media, MultipartFile thumbnail, CreateMediaContentDto createMediaContentDto);
    void delete(long id,String userEmail);
    MediaContentCreatedDto update(UpdateMediaDto updateMediaDto);
}
