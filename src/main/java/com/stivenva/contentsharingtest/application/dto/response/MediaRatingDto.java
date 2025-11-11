package com.stivenva.contentsharingtest.application.dto.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MediaRatingDto {

    public Double stars;
    public String comment;
    public String userFirstName;
    public String userLastName;
    public long userId;

}
