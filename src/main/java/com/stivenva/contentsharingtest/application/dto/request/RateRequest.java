package com.stivenva.contentsharingtest.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RateRequest {

    public Double stars;
    public String comment;
    public String username;
    public Long mediaContentId;
}
