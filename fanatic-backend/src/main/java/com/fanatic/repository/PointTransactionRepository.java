package com.fanatic.repository;

import com.fanatic.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID> {

    // ========== FIND BY USER ==========
    Page<PointTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<PointTransaction> findByUserId(UUID userId, Pageable pageable);

    // ========== FIND BY REASON ==========
    Page<PointTransaction> findByUserIdAndReason(UUID userId, String reason, Pageable pageable);

    // ========== TOTAL POINTS FOR USER ==========
    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt WHERE pt.user.id = :userId")
    int getTotalPointsByUserId(@Param("userId") UUID userId);

    // ========== TOTAL POINTS SINCE DATE ==========
    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt " +
           "WHERE pt.user.id = :userId AND pt.createdAt >= :since")
    int getPointsSince(@Param("userId") UUID userId, @Param("since") ZonedDateTime since);

    // ========== COUNT ==========
    long countByUserId(UUID userId);

    // ========== FIND BY REFERENCE ==========
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.referenceType = :type AND pt.referenceId = :refId")
    List<PointTransaction> findByReference(
            @Param("type") String type,
            @Param("refId") UUID refId);

    // ========== CHECK IF POINTS ALREADY AWARDED ==========
    @Query("SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END FROM PointTransaction pt " +
           "WHERE pt.user.id = :userId AND pt.referenceType = :type AND pt.referenceId = :refId")
    boolean existsByUserAndReference(
            @Param("userId") UUID userId,
            @Param("type") String type,
            @Param("refId") UUID refId);
}