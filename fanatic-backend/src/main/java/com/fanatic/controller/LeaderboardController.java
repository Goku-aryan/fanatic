package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.leaderboard.LeaderboardDTO;
import com.fanatic.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Ranking APIs")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get overall leaderboard")
    public ResponseEntity<ApiResponse<List<LeaderboardDTO>>> getOverallLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {

        List<LeaderboardDTO> leaderboard = leaderboardService.getOverallLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get leaderboard by content type (MOVIE, SERIES, BOOK)")
    public ResponseEntity<ApiResponse<List<LeaderboardDTO>>> getLeaderboardByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "50") int limit) {

        List<LeaderboardDTO> leaderboard = leaderboardService.getLeaderboardByType(type, limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/reviewers/{type}")
    @Operation(summary = "Get top reviewers by content type")
    public ResponseEntity<ApiResponse<List<LeaderboardDTO>>> getReviewLeaderboard(
            @PathVariable String type,
            @RequestParam(defaultValue = "50") int limit) {

        List<LeaderboardDTO> leaderboard = leaderboardService.getReviewLeaderboard(type, limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }
}