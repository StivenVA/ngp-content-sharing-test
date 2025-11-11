package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.application.port.media.CreateMediaContent;
import com.stivenva.contentsharingtest.application.port.media.DeleteMediaContent;
import com.stivenva.contentsharingtest.application.port.media.MediaContentFilter;
import com.stivenva.contentsharingtest.application.port.media.MediaContentService;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.PageResult;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MediaContentServiceImpl implements MediaContentService {

    private final MediaContentFilter mediaContentFilterService;
    private final CreateMediaContent createMediaContentService;
    private final DeleteMediaContent deleteMediaContentService;

    @Override
    public PageResult<MediaContent> filter(FilterMediaContentDto filterMediaContentDto) {
        return mediaContentFilterService.filter(filterMediaContentDto);
    }

    @Override
    public MediaContentCreatedDto create(MultipartFile media, MultipartFile thumbnail, CreateMediaContentDto createMediaContentDto) {
        return createMediaContentService.create(media, thumbnail, createMediaContentDto);
    }

    @Override
    public void delete(long id,String userEmail) {
        deleteMediaContentService.delete(id,userEmail);
    }

    @Override
    public MediaContentCreatedDto update(UpdateMediaDto updateMediaDto) {
        return createMediaContentService.update(updateMediaDto);
    }


}
