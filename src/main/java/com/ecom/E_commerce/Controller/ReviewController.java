package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Model.Review;
import com.ecom.E_commerce.Request.ReviewRequest;
import com.ecom.E_commerce.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> submitReview(
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Review saved = reviewService.submitReview(request, token);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getReviewsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(reviewService.getReviewsByItem(itemId));
    }
}
