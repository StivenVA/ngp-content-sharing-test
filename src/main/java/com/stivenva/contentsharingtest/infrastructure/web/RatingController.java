package com.stivenva.contentsharingtest.infrastructure.web;

import com.stivenva.contentsharingtest.application.dto.request.EditRateRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RateRequest;
import com.stivenva.contentsharingtest.application.port.rating.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("rate")
    public ResponseEntity<Void> rateMediaContent(
            @RequestBody RateRequest rateRequest,
            @AuthenticationPrincipal String username
    ) {
        rateRequest.username = username;

        ratingService.rate(rateRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("edit")
    public ResponseEntity<Void> editRate(
            @RequestBody EditRateRequestDto editRateRequest,
            @AuthenticationPrincipal String username

    ) {

        editRateRequest.username = username;
        ratingService.editRate(editRateRequest);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id,
            @AuthenticationPrincipal String username
    ){
        ratingService.deleteRating(username,id);

        return ResponseEntity.ok().build();
    }
}
