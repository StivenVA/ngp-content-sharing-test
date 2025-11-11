package com.stivenva.contentsharingtest.application.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class FilterMediaContentDto {

    public String title;
    public String description;
    public String category;
    public int page;
    public int size;

}
