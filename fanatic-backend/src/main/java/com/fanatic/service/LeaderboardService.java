package com.fanatic.service;

import com.fanatic.dto.leaderboard.LeaderboardDTO;
import com.fanatic.entity.ContentType;
import com.fanatic.entity.User;
import com.fanatic.exception.BadRequestException;
import com.fanatic.repository.ReviewRepository;
import com.fanatic.repository.UserContentRepository;
import com.fanatic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final UserContentRepository userContentRepository;
    private final ReviewRepository reviewRepository;

    // ========== OVERALL LEADERBOARD (by points) ==========
    @Transactional(readOnly = true)
    public List<LeaderboardDTO> getOverallLeaderboard(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<User> topUsers = userRepository.findTopByPoints(pageable);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardDTO> leaderboard = new ArrayList<>();

        topUsers.getContent().forEach(user -> {
            long contentCount = userContentRepository.countByUserId(user.getId());
            long reviewCount = reviewRepository.countByUserId(user.getId());

            leaderboard.add(LeaderboardDTO.builder()
                    .rank(rank.getAndIncrement())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .avatarUrl(user.getAvatarUrl())
                    .isVerified(user.getIsVerified())
                    .level(user.getLevel())
                    .points(user.getPoints())
                    .contentCount(contentCount)
                    .reviewCount(reviewCount)
                    .build());
        });

        return leaderboard;
    }

    // ========== LEADERBOARD BY CONTENT TYPE ==========
    @Transactional(readOnly = true)
    public List<LeaderboardDTO> getLeaderboardByType(String type, int limit) {
        ContentType contentType;
        try {
            contentType = ContentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid type: " + type + ". Use MOVIE, SERIES, or BOOK");
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> topUsers = userContentRepository.findTopUsersByContentType(contentType, pageable);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardDTO> leaderboard = new ArrayList<>();

        topUsers.forEach(row -> {
            UUID userId = (UUID) row[0];
            Long contentCount = (Long) row[1];

            Optional<User> userOpt = userRepository.findById(userId);
            userOpt.ifPresent(user -> {
                long reviewCount = reviewRepository.countByUserId(user.getId());

                leaderboard.add(LeaderboardDTO.builder()
                        .rank(rank.getAndIncrement())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .isVerified(user.getIsVerified())
                        .level(user.getLevel())
                        .points(user.getPoints())
                        .contentCount(contentCount)
                        .reviewCount(reviewCount)
                        .build());
            });
        });

        return leaderboard;
    }

    // ========== LEADERBOARD BY REVIEWS ==========
    @Transactional(readOnly = true)
    public List<LeaderboardDTO> getReviewLeaderboard(String type, int limit) {
        ContentType contentType;
        try {
            contentType = ContentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid type: " + type);
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> topReviewers = reviewRepository.findTopReviewersByContentType(contentType, pageable);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardDTO> leaderboard = new ArrayList<>();

        topReviewers.forEach(row -> {
            UUID userId = (UUID) row[0];
            Long reviewCount = (Long) row[1];

            Optional<User> userOpt = userRepository.findById(userId);
            userOpt.ifPresent(user -> {
                long contentCount = userContentRepository
                        .countByUserIdAndContentType(user.getId(), contentType);

                leaderboard.add(LeaderboardDTO.builder()
                        .rank(rank.getAndIncrement())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .isVerified(user.getIsVerified())
                        .level(user.getLevel())
                        .points(user.getPoints())
                        .contentCount(contentCount)
                        .reviewCount(reviewCount)
                        .build());
            });
        });

        return leaderboard;
    }
}