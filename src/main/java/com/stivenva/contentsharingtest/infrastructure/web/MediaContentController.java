package com.stivenva.contentsharingtest.infrastructure.web;

import com.stivenva.contentsharingtest.application.dto.CreateMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.FilterMediaContentDto;
import com.stivenva.contentsharingtest.application.dto.request.UpdateMediaDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentCreatedDto;
import com.stivenva.contentsharingtest.application.dto.response.MediaContentResponseDto;
import com.stivenva.contentsharingtest.application.port.media.MediaContentService;
import com.stivenva.contentsharingtest.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/media")
@RequiredArgsConstructor
public class MediaContentController {

    private final MediaContentService mediaContentService;

    @GetMapping("/{id}")
    public ResponseEntity<MediaContentResponseDto> getMediaContent(@PathVariable long id) {
        return ResponseEntity.ok(mediaContentService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<MediaContentResponseDto>> getAllMediaContent(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(mediaContentService.findAll(page,size));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<MediaContentResponseDto>> getMediaContentByUser(@RequestParam int page, @RequestParam int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        String username = auth.getName();

        return ResponseEntity.ok(mediaContentService.findByUserId(username,page,size));
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<MediaContentResponseDto>> filter(@RequestBody FilterMediaContentDto filterMediaContentDto) {

        Page<MediaContentResponseDto> result = mediaContentService.filter(filterMediaContentDto);

        if (result == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/create")
    public ResponseEntity<MediaContentCreatedDto> create(
            @RequestPart("mediaContent") MultipartFile mediaContent,
            @RequestPart(value = "thumbnail",required = false) MultipartFile thumbnail,
            @RequestParam("description") String description,
            @RequestParam("category") Category category,
            @RequestParam("title") String title
    ) {
        CreateMediaContentDto dto = new CreateMediaContentDto();
        dto.title = title;
        dto.description = description;
        dto.category = category;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        dto.username = auth.getName();

        MediaContentCreatedDto saved = mediaContentService.create(mediaContent, thumbnail, dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<MediaContentCreatedDto> updateMediaContent(
            @RequestPart(required = false) MultipartFile newThumbnail,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Category category,
            @PathVariable long id
            ){

        UpdateMediaDto dto = new UpdateMediaDto();
        dto.id = id;
        dto.title = title;
        dto.description = description;
        dto.category = category!=null?category.name():null;
        dto.thumbnail = newThumbnail;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        dto.username = auth.getName();

        mediaContentService.update(dto);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        String username = auth.getName();

        mediaContentService.delete(id,username);
        return ResponseEntity.ok().build();
    }

}
