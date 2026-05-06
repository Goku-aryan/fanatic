package com.fanatic.repository;

import com.fanatic.entity.ContentType;
import com.fanatic.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // ========== FIND ==========
    Page<Review> findByContentId(UUID contentId, Pageable pageable);

    Page<Review> findByUserId(UUID userId, Pageable pageable);

    Optional<Review> findByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== EXISTS ==========
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== COUNT ==========
    long countByUserId(UUID userId);

    long countByContentId(UUID contentId);

    long countByIsEarlyAccessReview(boolean isEarlyAccess);

    // ========== FIND BY CONTENT TYPE ==========
    @Query("SELECT r FROM Review r WHERE r.content.contentType = :type ORDER BY r.createdAt DESC")
    Page<Review> findByContentType(@Param("type") ContentType type, Pageable pageable);

    // ========== FIND USER REVIEWS BY CONTENT TYPE ==========
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.content.contentType = :type " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdAndContentType(
            @Param("userId") UUID userId,
            @Param("type") ContentType type,
            Pageable pageable);

    // ========== TOP REVIEWS (Most Liked) ==========
    @Query("SELECT r FROM Review r ORDER BY r.likesCount DESC")
    Page<Review> findTopLikedReviews(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.content.id = :contentId ORDER BY r.likesCount DESC")
    Page<Review> findTopLikedByContent(@Param("contentId") UUID contentId, Pageable pageable);

    // ========== EARLY ACCESS REVIEWS ==========
    @Query("SELECT r FROM Review r WHERE r.isEarlyAccessReview = true ORDER BY r.createdAt DESC")
    Page<Review> findEarlyAccessReviews(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.content.id = :contentId AND r.isEarlyAccessReview = true " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findEarlyAccessReviewsByContent(@Param("contentId") UUID contentId, Pageable pageable);

    // ========== RECENT REVIEWS ==========
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findRecentReviews(Pageable pageable);

    // ========== UPDATE LIKES COUNT ==========
    @Modifying
    @Query("UPDATE Review r SET r.likesCount = r.likesCount + 1 WHERE r.id = :reviewId")
    void incrementLikesCount(@Param("reviewId") UUID reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.likesCount = GREATEST(r.likesCount - 1, 0) WHERE r.id = :reviewId")
    void decrementLikesCount(@Param("reviewId") UUID reviewId);

    // ========== LEADERBOARD: Top reviewers by type ==========
    @Query("SELECT r.user.id, COUNT(r) as reviewCount " +
           "FROM Review r WHERE r.content.contentType = :type " +
           "GROUP BY r.user.id ORDER BY reviewCount DESC")
    List<Object[]> findTopReviewersByContentType(@Param("type") ContentType type, Pageable pageable);

    // ========== REVIEWS FROM FOLLOWED USERS ==========
    @Query("SELECT r FROM Review r WHERE r.user.id IN " +
           "(SELECT f.followingUser.id FROM Follower f WHERE f.followerUser.id = :userId) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findReviewsFromFollowedUsers(@Param("userId") UUID userId, Pageable pageable);
}