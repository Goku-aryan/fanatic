package com.fanatic.controller;

import com.fanatic.dto.common.ApiResponse;
import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.content.ContentCreateRequest;
import com.fanatic.dto.content.ContentDTO;
import com.fanatic.service.ContentService;
import com.fanatic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Movies, Series & Books APIs")
public class ContentController {

    private final ContentService contentService;
    private final UserService userService;

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping
    @Operation(summary = "Get all content")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getAllContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        PagedResponse<ContentDTO> content = contentService.getAllContent(page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/{contentId}")
    @Operation(summary = "Get content by ID")
    public ResponseEntity<ApiResponse<ContentDTO>> getContentById(
            @PathVariable UUID contentId) {

        ContentDTO content = contentService.getContentById(contentId);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get content by type (MOVIE, SERIES, BOOK)")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getContentByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ContentDTO> content = contentService.getContentByType(type, page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/search")
    @Operation(summary = "Search content")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> searchContent(
            @RequestParam String query,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ContentDTO> content = contentService.searchContent(query, type, page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated content")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getTopRated(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ContentDTO> content = contentService.getTopRated(type, page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/early-access")
    @Operation(summary = "Get early access content (First Look)")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getEarlyAccess(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ContentDTO> content = contentService.getEarlyAccessContent(page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    // ========== AUTHENTICATED ENDPOINTS ==========

    @PostMapping("/add/{contentId}")
    @Operation(summary = "Add content to my watched/read list")
    public ResponseEntity<ApiResponse<ContentDTO>> addToMyList(
            @PathVariable UUID contentId) {

        ContentDTO content = contentService.addContentToUserList(contentId);
        return ResponseEntity.ok(ApiResponse.success("Added to your list! Points earned!", content));
    }

    @DeleteMapping("/remove/{contentId}")
    @Operation(summary = "Remove content from my list")
    public ResponseEntity<ApiResponse<Void>> removeFromMyList(
            @PathVariable UUID contentId) {

        contentService.removeContentFromUserList(contentId);
        return ResponseEntity.ok(ApiResponse.success("Removed from your list", null));
    }

    @GetMapping("/my-list")
    @Operation(summary = "Get my content list")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getMyList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = userService.getCurrentUserId();
        PagedResponse<ContentDTO> content = contentService.getUserContentList(userId, type, page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/user/{userId}/list")
    @Operation(summary = "Get a user's content list")
    public ResponseEntity<ApiResponse<PagedResponse<ContentDTO>>> getUserList(
            @PathVariable UUID userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ContentDTO> content = contentService.getUserContentList(userId, type, page, size);
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    // ========== ADMIN ENDPOINTS ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create content (Admin)")
    public ResponseEntity<ApiResponse<ContentDTO>> createContent(
            @Valid @RequestBody ContentCreateRequest request) {

        ContentDTO content = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Content created!", content));
    }

    @PutMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update content (Admin)")
    public ResponseEntity<ApiResponse<ContentDTO>> updateContent(
            @PathVariable UUID contentId,
            @Valid @RequestBody ContentCreateRequest request) {

        ContentDTO content = contentService.updateContent(contentId, request);
        return ResponseEntity.ok(ApiResponse.success("Content updated!", content));
    }

    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete content (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
            @PathVariable UUID contentId) {

        contentService.deleteContent(contentId);
        return ResponseEntity.ok(ApiResponse.success("Content deleted!", null));
    }
}