package com.fanatic.repository;

import com.fanatic.entity.User;
import com.fanatic.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // ========== FIND ==========
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    // ========== EXISTS ==========
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // ========== SEARCH ==========
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    // ========== LEADERBOARD ==========
    @Query("SELECT u FROM User u ORDER BY u.points DESC")
    Page<User> findTopByPoints(Pageable pageable);

    // ========== COUNT ==========
    long countByRole(UserRole role);

    long countByIsActive(boolean isActive);

    long countByIsVerified(boolean isVerified);

    long countByCreatedAtAfter(ZonedDateTime date);

    // ========== ADMIN STATS ==========
    @Query("SELECT CAST(u.createdAt AS date) as date, COUNT(u) as count FROM User u " +
           "WHERE u.createdAt >= :since GROUP BY CAST(u.createdAt AS date) ORDER BY CAST(u.createdAt AS date)")
    List<Object[]> getUserGrowth(@Param("since") ZonedDateTime since);

    // ========== UPDATE POINTS ==========
    @Modifying
    @Query("UPDATE User u SET u.points = u.points + :points WHERE u.id = :userId")
    void addPoints(@Param("userId") UUID userId, @Param("points") int points);

    // ========== FIND RECENT ==========
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(Pageable pageable);

    // ========== FIND ALL ACTIVE ==========
    Page<User> findByIsActive(boolean isActive, Pageable pageable);

    // ========== FIND BY ROLE ==========
    Page<User> findByRole(UserRole role, Pageable pageable);
}