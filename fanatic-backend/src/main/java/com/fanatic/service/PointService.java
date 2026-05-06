package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.entity.LevelThreshold;
import com.fanatic.entity.PointTransaction;
import com.fanatic.entity.User;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.LevelThresholdRepository;
import com.fanatic.repository.PointTransactionRepository;
import com.fanatic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;
    private final LevelThresholdRepository levelThresholdRepository;

    @Value("${fanatic.points.per-add:10}")
    private int pointsPerAdd;

    @Value("${fanatic.points.per-review:25}")
    private int pointsPerReview;

    @Value("${fanatic.points.per-early-review:50}")
    private int pointsPerEarlyReview;

    // ========== GETTERS ==========
    public int getPointsPerAdd() { return pointsPerAdd; }
    public int getPointsPerReview() { return pointsPerReview; }
    public int getPointsPerEarlyReview() { return pointsPerEarlyReview; }

    // ========== AWARD POINTS ==========
    @Transactional
    public void awardPoints(UUID userId, int points, String reason,
                            String referenceType, UUID referenceId) {

        // Check if already awarded for this reference
        if (referenceType != null && referenceId != null) {
            boolean alreadyAwarded = pointTransactionRepository
                    .existsByUserAndReference(userId, referenceType, referenceId);
            if (alreadyAwarded) return;
        }

        // Create transaction
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .points(points)
                .reason(reason)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        pointTransactionRepository.save(transaction);

        // Update user points
        user.setPoints(user.getPoints() + points);

        // Update level
        Optional<LevelThreshold> levelOpt = levelThresholdRepository.findLevelForPoints(user.getPoints());
        levelOpt.ifPresent(level -> user.setLevel(level.getLevel()));

        userRepository.save(user);
    }

    // ========== GET POINT HISTORY ==========
    @Transactional(readOnly = true)
    public PagedResponse<Map<String, Object>> getPointHistory(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointTransaction> transactions =
                pointTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<Map<String, Object>> content = transactions.getContent().stream()
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", t.getId());
                    map.put("points", t.getPoints());
                    map.put("reason", t.getReason());
                    map.put("referenceType", t.getReferenceType());
                    map.put("createdAt", t.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return PagedResponse.<Map<String, Object>>builder()
                .content(content)
                .page(transactions.getNumber())
                .size(transactions.getSize())
                .totalElements(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .last(transactions.isLast())
                .build();
    }

    // ========== GET LEVEL INFO ==========
    @Transactional(readOnly = true)
    public Map<String, Object> getLevelInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Map<String, Object> info = new HashMap<>();
        info.put("currentLevel", user.getLevel());
        info.put("currentPoints", user.getPoints());

        Optional<LevelThreshold> currentLevel = levelThresholdRepository.findById(user.getLevel());
        currentLevel.ifPresent(l -> {
            info.put("levelTitle", l.getTitle());
            info.put("levelIcon", l.getIcon());
        });

        Optional<LevelThreshold> nextLevel = levelThresholdRepository.findNextLevel(user.getPoints());
        nextLevel.ifPresent(l -> {
            info.put("nextLevel", l.getLevel());
            info.put("nextLevelTitle", l.getTitle());
            info.put("pointsToNextLevel", l.getPointsRequired() - user.getPoints());
            info.put("nextLevelPoints", l.getPointsRequired());
        });

        return info;
    }

    // ========== GET ALL LEVELS ==========
    @Transactional(readOnly = true)
    public List<LevelThreshold> getAllLevels() {
        return levelThresholdRepository.findAllByOrderByLevelAsc();
    }
}