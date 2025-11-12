package com.stivenva.contentsharingtest.application.port.media;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface MediaContentService {
    Page<MediaContentResponseDto> filter(FilterMediaContentDto filterMediaContentDto);
    MediaContentCreatedDto create(MultipartFile media, MultipartFile thumbnail, CreateMediaContentDto createMediaContentDto);
    void delete(long id,String username);
    MediaContentCreatedDto update(UpdateMediaDto updateMediaDto);
    MediaContentResponseDto findById(long id);
    Page<MediaContentResponseDto> findAll(int page, int size);
    Page<MediaContentResponseDto> findByUserId(String username, int page, int size);

}
