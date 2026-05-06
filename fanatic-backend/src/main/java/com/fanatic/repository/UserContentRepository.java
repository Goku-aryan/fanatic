package com.fanatic.repository;

import com.fanatic.entity.ContentType;
import com.fanatic.entity.UserContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserContentRepository extends JpaRepository<UserContent, UUID> {

    // ========== FIND ==========
    Optional<UserContent> findByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== EXISTS ==========
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== FIND BY USER ==========
    Page<UserContent> findByUserId(UUID userId, Pageable pageable);

    // ========== FIND BY USER + TYPE ==========
    @Query("SELECT uc FROM UserContent uc WHERE uc.user.id = :userId " +
           "AND uc.content.contentType = :type ORDER BY uc.addedAt DESC")
    Page<UserContent> findByUserIdAndContentType(
            @Param("userId") UUID userId,
            @Param("type") ContentType type,
            Pageable pageable);

    // ========== COUNT BY USER + TYPE ==========
    @Query("SELECT COUNT(uc) FROM UserContent uc WHERE uc.user.id = :userId " +
           "AND uc.content.contentType = :type")
    long countByUserIdAndContentType(
            @Param("userId") UUID userId,
            @Param("type") ContentType type);

    // ========== COUNT BY USER ==========
    long countByUserId(UUID userId);

    // ========== TOTAL POINTS EARNED BY USER ==========
    @Query("SELECT COALESCE(SUM(uc.pointsEarned), 0) FROM UserContent uc WHERE uc.user.id = :userId")
    int totalPointsEarnedByUser(@Param("userId") UUID userId);

    // ========== LEADERBOARD: Top users by content type count ==========
    @Query("SELECT uc.user.id, COUNT(uc) as contentCount " +
           "FROM UserContent uc WHERE uc.content.contentType = :type " +
           "GROUP BY uc.user.id ORDER BY contentCount DESC")
    List<Object[]> findTopUsersByContentType(@Param("type") ContentType type, Pageable pageable);

    // ========== RECENTLY ADDED BY USER ==========
    @Query("SELECT uc FROM UserContent uc WHERE uc.user.id = :userId ORDER BY uc.addedAt DESC")
    Page<UserContent> findRecentByUserId(@Param("userId") UUID userId, Pageable pageable);

    // ========== FIND CONTENT IDS ADDED BY USER ==========
    @Query("SELECT uc.content.id FROM UserContent uc WHERE uc.user.id = :userId")
    List<UUID> findContentIdsByUserId(@Param("userId") UUID userId);
}