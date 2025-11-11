package com.stivenva.contentsharingtest.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class MediaContentResponseDto {

    public long id;
    public String title;
    public String description;
    public String category;
    public String thumbnailUrl;
    public String mediaUrl;
    public List<MediaRatingDto> ratings;
}
