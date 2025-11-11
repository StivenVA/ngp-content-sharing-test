package com.stivenva.contentsharingtest.infrastructure.web;

import com.stivenva.contentsharingtest.application.dto.request.EditRateRequestDto;
import com.stivenva.contentsharingtest.application.dto.request.RateRequest;
import com.stivenva.contentsharingtest.application.port.rating.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.Response;

@RestController
@RequestMapping("api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("rate")
    public ResponseEntity<Void> rateMediaContent(@RequestBody RateRequest rateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        rateRequest.username = authentication.getName();

        ratingService.rate(rateRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("edit")
    public ResponseEntity<Void> editRate(@RequestBody EditRateRequestDto editRateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        editRateRequest.username = authentication.getName();
        ratingService.editRate(editRateRequest);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RuntimeException("Unauthenticated user");
        }

        String username = authentication.getName();

        ratingService.deleteRating(username,id);

        return ResponseEntity.ok().build();
    }
}
