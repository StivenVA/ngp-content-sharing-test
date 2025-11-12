package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.application.port.media.CreateMediaContent;
import com.stivenva.contentsharingtest.domain.model.Category;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.StorageService;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.stivenva.contentsharingtest.application.util.S3UrlKeyExtractor.extractKeyFromUrl;

@Service
@RequiredArgsConstructor
public class CreateMediaContentService implements CreateMediaContent {

    private final StorageService storageService;
    private final MediaContentRepository mediaContentRepository;
    private final UserRepository userRepository;


    @Override
    public MediaContentCreatedDto create(MultipartFile media, MultipartFile thumbnail, CreateMediaContentDto createMediaContentDto) {
        try {
            if (media == null || media.isEmpty()) {
                throw new IllegalArgumentException("Media file is required");
            }

            User userMediaCreator = userRepository.findByUsername(createMediaContentDto.username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + createMediaContentDto.username));

            String mediaKey = buildObjectKey("media", media.getOriginalFilename());

            String thumbnailUrl = null;

            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailKey = buildObjectKey("thumbnails", thumbnail.getOriginalFilename());
                thumbnailUrl = storageService.upload(thumbnail.getBytes(), thumbnailKey,thumbnail.getContentType());
            }

            String mediaUrl = storageService.upload(media.getBytes(), mediaKey,media.getContentType());

            return buildMediaContentCreatedDto(
                    createMediaContentDto,
                    thumbnailUrl,
                    mediaUrl,
                    userMediaCreator.id()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload files", e);
        }
    }

    @Override
    public MediaContentCreatedDto update(UpdateMediaDto updateMediaDto) {

        MediaContent mediaToUpdate = mediaContentRepository.findById(updateMediaDto.id)
                .orElseThrow(() -> new RuntimeException("Media not found: " + updateMediaDto.id));

        mediaToUpdate = updateMediaFields(updateMediaDto,mediaToUpdate);

        if (updateMediaDto.thumbnail != null) {
            updateThumbnail(mediaToUpdate.thumbnailUrl(), updateMediaDto.thumbnail);
        }

        mediaContentRepository.save(mediaToUpdate);

        return buildMediaContentCreatedDto(mediaToUpdate);
    }

    private void updateThumbnail(String thumbnailUrl, MultipartFile newThumbnail){
        String existingKey = extractKeyFromUrl(thumbnailUrl);

        try {
            storageService.upload(
                    newThumbnail.getBytes(),
                    existingKey,
                    newThumbnail.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public MediaContent updateMediaFields(UpdateMediaDto updateMediaDto,MediaContent mediaToUpdate){

        return new MediaContent(
                mediaToUpdate.id(),
                mediaToUpdate.userId(),
                updateMediaDto.title != null ? updateMediaDto.title : mediaToUpdate.title(),
                updateMediaDto.description != null ? updateMediaDto.description : mediaToUpdate.description(),
                updateMediaDto.category != null ? Category.valueOf(updateMediaDto.category) : mediaToUpdate.category(),
                mediaToUpdate.thumbnailUrl(),
                mediaToUpdate.contentUrl(),
                mediaToUpdate.createdAt()
        );

    }

    private MediaContentCreatedDto buildMediaContentCreatedDto(MediaContent mediaContent){
        return new MediaContentCreatedDto(
                mediaContent.id(),
                mediaContent.title(),
                mediaContent.description(),
                mediaContent.category().name(),
                mediaContent.thumbnailUrl(),
                mediaContent.contentUrl()
        );
    }

    private MediaContentCreatedDto buildMediaContentCreatedDto(CreateMediaContentDto mediaContent, String thumbnailUrl, String mediaUrl, long userId) {
        MediaContent content = new MediaContent(
                0L,
                userId,
                mediaContent.title,
                mediaContent.description,
                mediaContent.category,
                thumbnailUrl,
                mediaUrl,
                LocalDateTime.now()
        );

        MediaContent saved = mediaContentRepository.save(content);

        MediaContentCreatedDto result = new MediaContentCreatedDto();
        result.id = saved.id();
        result.title = saved.title();
        result.description = saved.description();
        result.category = saved.category().name();
        result.thumbnailUrl = saved.thumbnailUrl();
        result.mediaUrl = saved.contentUrl();

        return result;
    }

    private String buildObjectKey(String folder, String originalFilename) {
        String cleanFolder = folder == null ? "" : folder.replaceAll("^/+|/+$", "");
        String ext = extractExtension(originalFilename);
        String key = cleanFolder.isEmpty() ? UUID.randomUUID().toString() : cleanFolder + "/" + UUID.randomUUID();
        return ext.isBlank() ? key : key + "." + ext;
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx == -1 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1);
    }
}
