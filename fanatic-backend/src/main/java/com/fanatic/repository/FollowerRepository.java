package com.fanatic.repository;

import com.fanatic.entity.Follower;
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
public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    // ========== FIND ==========
    Optional<Follower> findByFollowerUserIdAndFollowingUserId(UUID followerId, UUID followingId);

    // ========== EXISTS ==========
    boolean existsByFollowerUserIdAndFollowingUserId(UUID followerId, UUID followingId);

    // ========== FIND FOLLOWERS OF A USER ==========
    // People who follow this user
    Page<Follower> findByFollowingUserId(UUID followingId, Pageable pageable);

    // ========== FIND FOLLOWING OF A USER ==========
    // People this user follows
    Page<Follower> findByFollowerUserId(UUID followerId, Pageable pageable);

    // ========== COUNT ==========
    long countByFollowingUserId(UUID followingId);  // followers count
    long countByFollowerUserId(UUID followerId);      // following count

    // ========== GET FOLLOWER IDS ==========
    @Query("SELECT f.followerUser.id FROM Follower f WHERE f.followingUser.id = :userId")
    List<UUID> findFollowerIdsByUserId(@Param("userId") UUID userId);

    // ========== GET FOLLOWING IDS ==========
    @Query("SELECT f.followingUser.id FROM Follower f WHERE f.followerUser.id = :userId")
    List<UUID> findFollowingIdsByUserId(@Param("userId") UUID userId);

    // ========== DELETE ==========
    void deleteByFollowerUserIdAndFollowingUserId(UUID followerId, UUID followingId);

    // ========== MUTUAL FOLLOWERS ==========
    @Query("SELECT f1.followingUser.id FROM Follower f1 " +
           "WHERE f1.followerUser.id = :userId " +
           "AND f1.followingUser.id IN " +
           "(SELECT f2.followerUser.id FROM Follower f2 WHERE f2.followingUser.id = :userId)")
    List<UUID> findMutualFollowers(@Param("userId") UUID userId);
}