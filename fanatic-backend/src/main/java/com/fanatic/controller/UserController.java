package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.user.UserDTO;
import com.fanatic.dto.user.UserProfileUpdateRequest;
import com.fanatic.service.PointService;
import com.fanatic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile & management APIs")
public class UserController {

    private final UserService userService;
    private final PointService pointService;

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<UserDTO>> getMyProfile() {
        UserDTO user = userService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyProfile(
            @RequestBody UserProfileUpdateRequest request) {

        UserDTO user = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated!", user));
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(
            @PathVariable UUID userId) {

        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user profile by username")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(
            @PathVariable String username) {

        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserDTO> users = userService.searchUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/me/points/history")
    @Operation(summary = "Get my point transaction history")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getPointHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = userService.getCurrentUserId();
        PagedResponse<Map<String, Object>> history = pointService.getPointHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/me/level")
    @Operation(summary = "Get my level info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLevelInfo() {
        UUID userId = userService.getCurrentUserId();
        Map<String, Object> levelInfo = pointService.getLevelInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(levelInfo));
    }

    @GetMapping("/levels")
    @Operation(summary = "Get all level thresholds")
    public ResponseEntity<ApiResponse<List<?>>> getAllLevels() {
        return ResponseEntity.ok(ApiResponse.success(pointService.getAllLevels()));
    }
}