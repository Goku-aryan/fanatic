package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.review.ReviewCreateRequest;
import com.fanatic.dto.review.ReviewDTO;
import com.fanatic.entity.*;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ContentRepository contentRepository;
    private final UserContentRepository userContentRepository;
    private final EarlyAccessGrantRepository earlyAccessGrantRepository;
    private final UserService userService;
    private final PointService pointService;

    // ========== CREATE REVIEW ==========
    @Transactional
    public ReviewDTO createReview(ReviewCreateRequest request) {
        User user = userService.getCurrentUser();
        UUID userId = user.getId();
        UUID contentId = request.getContentId();

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        // Check if already reviewed
        if (reviewRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new BadRequestException("You have already reviewed this content");
        }

        // Check if user has added content OR has early access
        boolean hasAdded = userContentRepository.existsByUserIdAndContentId(userId, contentId);
        boolean hasEarlyAccess = earlyAccessGrantRepository.existsByUserIdAndContentId(userId, contentId);

        if (!hasAdded && !hasEarlyAccess) {
            throw new BadRequestException("You must add this to your list or have early access before reviewing");
        }

        boolean isEarlyAccessReview = hasEarlyAccess && content.getIsEarlyAccess();
        int pointsEarned = isEarlyAccessReview ?
                pointService.getPointsPerEarlyReview() : pointService.getPointsPerReview();

        Review review = Review.builder()
                .user(user)
                .content(content)
                .title(request.getTitle())
                .body(request.getBody())
                .rating(request.getRating())
                .isEarlyAccessReview(isEarlyAccessReview)
                .isSpoiler(request.getIsSpoiler() != null ? request.getIsSpoiler() : false)
                .likesCount(0)
                .pointsEarned(pointsEarned)
                .build();

        Review saved = reviewRepository.save(review);

        // Award points
        String reason = isEarlyAccessReview ?
                "Early access review: " + content.getTitle() :
                "Review: " + content.getTitle();
        pointService.awardPoints(userId, pointsEarned, reason, "REVIEW", saved.getId());

        return mapToReviewDTO(saved, userId);
    }

    // ========== UPDATE REVIEW ==========
    @Transactional
    public ReviewDTO updateReview(UUID reviewId, ReviewCreateRequest request) {
        UUID userId = userService.getCurrentUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only edit your own reviews");
        }

        if (request.getTitle() != null) review.setTitle(request.getTitle());
        if (request.getBody() != null) review.setBody(request.getBody());
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getIsSpoiler() != null) review.setIsSpoiler(request.getIsSpoiler());

        Review saved = reviewRepository.save(review);
        return mapToReviewDTO(saved, userId);
    }

    // ========== DELETE REVIEW ==========
    @Transactional
    public void deleteReview(UUID reviewId) {
        UUID userId = userService.getCurrentUserId();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    // ========== GET REVIEWS BY CONTENT ==========
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDTO> getReviewsByContent(UUID contentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviews = reviewRepository.findByContentId(contentId, pageable);
        return mapToPagedResponse(reviews);
    }

    // ========== GET REVIEWS BY USER ==========
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDTO> getReviewsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);
        return mapToPagedResponse(reviews);
    }

    // ========== GET FEED (Reviews from followed users) ==========
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDTO> getReviewFeed(int page, int size) {
        UUID userId = userService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findReviewsFromFollowedUsers(userId, pageable);
        return mapToPagedResponse(reviews);
    }

    // ========== LIKE REVIEW ==========
    @Transactional
    public ReviewDTO toggleLike(UUID reviewId) {
        UUID userId = userService.getCurrentUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        boolean alreadyLiked = reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);

        if (alreadyLiked) {
            reviewLikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
            reviewRepository.decrementLikesCount(reviewId);
        } else {
            ReviewLike like = ReviewLike.builder()
                    .user(User.builder().id(userId).build())
                    .review(review)
                    .build();
            reviewLikeRepository.save(like);
            reviewRepository.incrementLikesCount(reviewId);
        }

        // Refresh review
        review = reviewRepository.findById(reviewId).get();
        return mapToReviewDTO(review, userId);
    }

    // ========== GET EARLY ACCESS REVIEWS ==========
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDTO> getEarlyAccessReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviews = reviewRepository.findEarlyAccessReviews(pageable);
        return mapToPagedResponse(reviews);
    }

    // ========== MAP TO DTO ==========
    public ReviewDTO mapToReviewDTO(Review review, UUID currentUserId) {
        boolean userHasLiked = false;
        if (currentUserId != null) {
            userHasLiked = reviewLikeRepository.existsByUserIdAndReviewId(currentUserId, review.getId());
        }

        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .userAvatarUrl(review.getUser().getAvatarUrl())
                .userIsVerified(review.getUser().getIsVerified())
                .contentId(review.getContent().getId())
                .contentTitle(review.getContent().getTitle())
                .contentType(review.getContent().getContentType().name())
                .title(review.getTitle())
                .body(review.getBody())
                .rating(review.getRating())
                .isEarlyAccessReview(review.getIsEarlyAccessReview())
                .isSpoiler(review.getIsSpoiler())
                .likesCount(review.getLikesCount())
                .userHasLiked(userHasLiked)
                .pointsEarned(review.getPointsEarned())
                .createdAt(review.getCreatedAt())
                .build();
    }

    // ========== MAP TO PAGED RESPONSE ==========
    private PagedResponse<ReviewDTO> mapToPagedResponse(Page<Review> page) {
        UUID currentUserId = null;
        try {
            currentUserId = userService.getCurrentUserId();
        } catch (Exception ignored) {}

        UUID finalUserId = currentUserId;
        List<ReviewDTO> content = page.getContent().stream()
                .map(r -> mapToReviewDTO(r, finalUserId))
                .collect(Collectors.toList());

        return PagedResponse.<ReviewDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}