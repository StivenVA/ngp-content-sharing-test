package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentResponseDto;
import com.stivenva.contentsharingtest.application.port.media.CreateMediaContent;
import com.stivenva.contentsharingtest.application.port.media.DeleteMediaContent;
import com.stivenva.contentsharingtest.application.port.media.MediaContentFilter;
import com.stivenva.contentsharingtest.application.port.media.MediaContentService;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MediaContentServiceImpl implements MediaContentService {

    private final MediaContentFilter mediaContentFilterService;
    private final CreateMediaContent createMediaContentService;
    private final DeleteMediaContent deleteMediaContentService;
    private final UserRepository userRepository;

    @Override
    public Page<MediaContentResponseDto> filter(FilterMediaContentDto filterMediaContentDto) {
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

    @Override
    public MediaContentResponseDto findById(long id) {
        return mediaContentFilterService.findById(id);
    }

    public Page<MediaContentResponseDto> findAll(int page, int size) {
        return mediaContentFilterService.findAll(page,size);
    }

    public Page<MediaContentResponseDto> findByUserId(String username, int page, int size) {

        User mediaContentOwner = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mediaContentFilterService.findByUserId(mediaContentOwner.id(),page,size);
    }
}
