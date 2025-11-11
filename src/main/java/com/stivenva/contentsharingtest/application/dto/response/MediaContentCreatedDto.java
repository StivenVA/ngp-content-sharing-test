package com.stivenva.contentsharingtest.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MediaContentCreatedDto {

    public long id;
    public String title;
    public String description;
    public String category;
    public String thumbnailUrl;
    public String mediaUrl;
}
