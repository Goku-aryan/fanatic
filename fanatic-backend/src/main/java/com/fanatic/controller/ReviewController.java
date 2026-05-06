package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.review.ReviewCreateRequest;
import com.fanatic.dto.review.ReviewDTO;
import com.fanatic.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a review")
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewDTO review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review posted! Points earned!", review));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update my review")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewDTO review = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated!", review));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete my review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable UUID reviewId) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted!", null));
    }

    @GetMapping("/content/{contentId}")
    @Operation(summary = "Get reviews for content")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getReviewsByContent(
            @PathVariable UUID contentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ReviewDTO> reviews = reviewService.getReviewsByContent(contentId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getReviewsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ReviewDTO> reviews = reviewService.getReviewsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/feed")
    @Operation(summary = "Get review feed from people I follow")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getReviewFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ReviewDTO> reviews = reviewService.getReviewFeed(page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping("/{reviewId}/like")
    @Operation(summary = "Like/Unlike a review")
    public ResponseEntity<ApiResponse<ReviewDTO>> toggleLike(
            @PathVariable UUID reviewId) {

        ReviewDTO review = reviewService.toggleLike(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @GetMapping("/early-access")
    @Operation(summary = "Get early access reviews (First Look Reviews)")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getEarlyAccessReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ReviewDTO> reviews = reviewService.getEarlyAccessReviews(page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}