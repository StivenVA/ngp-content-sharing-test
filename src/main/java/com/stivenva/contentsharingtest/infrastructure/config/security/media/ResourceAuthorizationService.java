package com.stivenva.contentsharingtest.infrastructure.config.security.media;

import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository;
import com.stivenva.contentsharingtest.domain.port.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("resourceAuthorizationService")
@RequiredArgsConstructor
public class ResourceAuthorizationService {

    private final UserRepository userRepository;
    private final MediaContentRepository mediaContentRepository;

    public boolean isMediaOwner(String username, Long mediaContentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        MediaContent media = mediaContentRepository.findById(mediaContentId)
                .orElseThrow(() -> new RuntimeException("Media not found: " + mediaContentId));

        return media.userId() == user.id();
    }
}