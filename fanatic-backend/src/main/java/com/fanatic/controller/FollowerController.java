package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.user.UserDTO;
import com.fanatic.service.FollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Followers", description = "Follow/Unfollow APIs")
public class FollowerController {

    private final FollowerService followerService;

    @PostMapping("/{userId}")
    @Operation(summary = "Follow a user")
    public ResponseEntity<ApiResponse<String>> followUser(
            @PathVariable UUID userId) {

        String message = followerService.followUser(userId);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @PathVariable UUID userId) {

        String message = followerService.unfollowUser(userId);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/check/{userId}")
    @Operation(summary = "Check if I follow a user")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkFollowing(
            @PathVariable UUID userId) {

        boolean isFollowing = followerService.isFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("isFollowing", isFollowing)));
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get followers of a user")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getFollowers(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserDTO> followers = followerService.getFollowers(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followers));
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "Get users followed by a user")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getFollowing(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserDTO> following = followerService.getFollowing(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(following));
    }

    @GetMapping("/{userId}/mutual")
    @Operation(summary = "Get mutual followers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getMutualFollowers(
            @PathVariable UUID userId) {

        List<UserDTO> mutual = followerService.getMutualFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success(mutual));
    }
}