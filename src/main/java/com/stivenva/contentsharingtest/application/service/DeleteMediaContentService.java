package com.stivenva.contentsharingtest.application.service;

import com.stivenva.contentsharingtest.application.port.media.DeleteMediaContent;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.StorageService;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.stivenva.contentsharingtest.application.util.S3UrlKeyExtractor.extractKeyFromUrl;

@Service
@RequiredArgsConstructor
public class DeleteMediaContentService implements DeleteMediaContent {

    private final MediaContentRepository mediaContentRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;

    @Override
    public void delete(long mediaContentId,String username) {

        User userMediaCreator = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username));

        MediaContent content = mediaContentRepository.findById(mediaContentId)
                .orElseThrow(() -> new RuntimeException("MediaContent not found with id: " + mediaContentId));

        if (content.userId() != userMediaCreator.id())
            throw new RuntimeException("User does not have permission to delete this media");

        String mediaKey = extractKeyFromUrl(content.contentUrl());
        String thumbnailKey = extractKeyFromUrl(content.thumbnailUrl());

        storageService.delete(mediaKey);
        storageService.delete(thumbnailKey);

        mediaContentRepository.delete(content);
    }
}
