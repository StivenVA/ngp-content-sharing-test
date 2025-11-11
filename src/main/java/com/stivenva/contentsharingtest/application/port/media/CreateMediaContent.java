package com.stivenva.contentsharingtest.application.port.media;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import org.springframework.web.multipart.MultipartFile;

public interface CreateMediaContent {

    MediaContentCreatedDto create(MultipartFile media, MultipartFile thumbnail, CreateMediaContentDto createMediaContentDto);
    MediaContentCreatedDto update(UpdateMediaDto updateMediaDto);
}
