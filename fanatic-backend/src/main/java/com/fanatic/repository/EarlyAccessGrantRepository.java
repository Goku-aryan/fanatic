package com.fanatic.repository;

import com.fanatic.entity.EarlyAccessGrant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EarlyAccessGrantRepository extends JpaRepository<EarlyAccessGrant, UUID> {

    // ========== FIND ==========
    Optional<EarlyAccessGrant> findByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== EXISTS ==========
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);

    // ========== CHECK VALID (not expired) ==========
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EarlyAccessGrant e " +
           "WHERE e.user.id = :userId AND e.content.id = :contentId " +
           "AND (e.expiresAt IS NULL OR e.expiresAt > :now)")
    boolean hasValidAccess(
            @Param("userId") UUID userId,
            @Param("contentId") UUID contentId,
            @Param("now") ZonedDateTime now);

    // ========== FIND BY USER ==========
    Page<EarlyAccessGrant> findByUserId(UUID userId, Pageable pageable);

    // ========== FIND BY CONTENT ==========
    Page<EarlyAccessGrant> findByContentId(UUID contentId, Pageable pageable);

    // ========== COUNT ==========
    long countByContentId(UUID contentId);

    long countByUserId(UUID userId);

    // ========== FIND CONTENT IDS BY USER ==========
    @Query("SELECT e.content.id FROM EarlyAccessGrant e WHERE e.user.id = :userId " +
           "AND (e.expiresAt IS NULL OR e.expiresAt > CURRENT_TIMESTAMP)")
    List<UUID> findActiveContentIdsByUserId(@Param("userId") UUID userId);
}