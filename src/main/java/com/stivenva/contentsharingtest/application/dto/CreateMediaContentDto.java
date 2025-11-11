package com.stivenva.contentsharingtest.application.dto;

import com.stivenva.contentsharingtest.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CreateMediaContentDto {
    public String title;
    public String description;
    public Category category;
    public String username;
}
