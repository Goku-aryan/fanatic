package com.fanatic.repository;

import com.fanatic.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

    // ========== FIND ==========
    Optional<ReviewLike> findByUserIdAndReviewId(UUID userId, UUID reviewId);

    // ========== EXISTS ==========
    boolean existsByUserIdAndReviewId(UUID userId, UUID reviewId);

    // ========== COUNT ==========
    long countByReviewId(UUID reviewId);

    // ========== FIND ALL LIKES BY USER ==========
    List<ReviewLike> findByUserId(UUID userId);

    // ========== FIND ALL LIKES FOR REVIEW ==========
    List<ReviewLike> findByReviewId(UUID reviewId);

    // ========== DELETE ==========
    void deleteByUserIdAndReviewId(UUID userId, UUID reviewId);
}