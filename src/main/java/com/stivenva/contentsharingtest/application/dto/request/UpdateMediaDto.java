package com.stivenva.contentsharingtest.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
public class UpdateMediaDto {

    public long id;
    public String title;
    public String description;
    public String category;
    public MultipartFile thumbnail;
    public String username;
}
