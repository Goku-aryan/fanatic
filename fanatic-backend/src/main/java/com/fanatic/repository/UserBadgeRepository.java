package com.fanatic.repository;

import com.fanatic.entity.BadgeType;
import com.fanatic.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {

    // ========== FIND BY USER ==========
    List<UserBadge> findByUserId(UUID userId);

    // ========== FIND SPECIFIC BADGE ==========
    Optional<UserBadge> findByUserIdAndBadgeType(UUID userId, BadgeType badgeType);

    // ========== EXISTS ==========
    boolean existsByUserIdAndBadgeType(UUID userId, BadgeType badgeType);

    // ========== COUNT ==========
    long countByUserId(UUID userId);

    long countByBadgeType(BadgeType badgeType);

    // ========== DELETE ==========
    void deleteByUserIdAndBadgeType(UUID userId, BadgeType badgeType);
}