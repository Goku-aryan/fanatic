package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.content.ContentCreateRequest;
import com.fanatic.dto.content.ContentDTO;
import com.fanatic.entity.Content;
import com.fanatic.entity.ContentType;
import com.fanatic.entity.User;
import com.fanatic.entity.UserContent;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserContentRepository userContentRepository;
    private final ReviewRepository reviewRepository;
    private final EarlyAccessGrantRepository earlyAccessGrantRepository;
    private final UserService userService;
    private final PointService pointService;

    // ========== CREATE CONTENT (Admin) ==========
    @Transactional
    public ContentDTO createContent(ContentCreateRequest request) {
        User currentUser = userService.getCurrentUser();

        ContentType type;
        try {
            type = ContentType.valueOf(request.getContentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid content type: " + request.getContentType());
        }

        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(type)
                .genre(request.getGenre())
                .coverImageUrl(request.getCoverImageUrl())
                .releaseDate(request.getReleaseDate())
                .authorDirector(request.getAuthorDirector())
                .isEarlyAccess(request.getIsEarlyAccess() != null ? request.getIsEarlyAccess() : false)
                .earlyAccessPrice(request.getEarlyAccessPrice())
                .earlyAccessStart(request.getEarlyAccessStart())
                .earlyAccessEnd(request.getEarlyAccessEnd())
                .createdBy(currentUser)
                .build();

        Content saved = contentRepository.save(content);
        return mapToContentDTO(saved, null);
    }

    // ========== UPDATE CONTENT (Admin) ==========
    @Transactional
    public ContentDTO updateContent(UUID contentId, ContentCreateRequest request) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        if (request.getTitle() != null) content.setTitle(request.getTitle());
        if (request.getDescription() != null) content.setDescription(request.getDescription());
        if (request.getGenre() != null) content.setGenre(request.getGenre());
        if (request.getCoverImageUrl() != null) content.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getReleaseDate() != null) content.setReleaseDate(request.getReleaseDate());
        if (request.getAuthorDirector() != null) content.setAuthorDirector(request.getAuthorDirector());
        if (request.getIsEarlyAccess() != null) content.setIsEarlyAccess(request.getIsEarlyAccess());
        if (request.getEarlyAccessPrice() != null) content.setEarlyAccessPrice(request.getEarlyAccessPrice());
        if (request.getEarlyAccessStart() != null) content.setEarlyAccessStart(request.getEarlyAccessStart());
        if (request.getEarlyAccessEnd() != null) content.setEarlyAccessEnd(request.getEarlyAccessEnd());

        Content saved = contentRepository.save(content);
        return mapToContentDTO(saved, null);
    }

    // ========== DELETE CONTENT (Admin) ==========
    @Transactional
    public void deleteContent(UUID contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new ResourceNotFoundException("Content", "id", contentId);
        }
        contentRepository.deleteById(contentId);
    }

    // ========== GET CONTENT BY ID ==========
    @Transactional(readOnly = true)
    public ContentDTO getContentById(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        UUID currentUserId = null;
        try {
            currentUserId = userService.getCurrentUserId();
        } catch (Exception ignored) {}

        return mapToContentDTO(content, currentUserId);
    }

    // ========== GET ALL CONTENT ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> getAllContent(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Content> contents = contentRepository.findAll(pageable);
        return mapToPagedResponse(contents);
    }

    // ========== GET CONTENT BY TYPE ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> getContentByType(String type, int page, int size) {
        ContentType contentType = ContentType.valueOf(type.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Content> contents = contentRepository.findByContentType(contentType, pageable);
        return mapToPagedResponse(contents);
    }

    // ========== SEARCH CONTENT ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> searchContent(String query, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contents;

        if (type != null && !type.isEmpty()) {
            ContentType contentType = ContentType.valueOf(type.toUpperCase());
            contents = contentRepository.searchContentByType(query, contentType, pageable);
        } else {
            contents = contentRepository.searchContent(query, pageable);
        }

        return mapToPagedResponse(contents);
    }

    // ========== GET EARLY ACCESS CONTENT ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> getEarlyAccessContent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Content> contents = contentRepository.findByIsEarlyAccess(true, pageable);
        return mapToPagedResponse(contents);
    }

    // ========== GET TOP RATED ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> getTopRated(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contents;

        if (type != null && !type.isEmpty()) {
            ContentType contentType = ContentType.valueOf(type.toUpperCase());
            contents = contentRepository.findTopRatedByType(contentType, pageable);
        } else {
            contents = contentRepository.findTopRated(pageable);
        }

        return mapToPagedResponse(contents);
    }

    // ========== ADD CONTENT TO USER LIST ==========
    @Transactional
    public ContentDTO addContentToUserList(UUID contentId) {
        User user = userService.getCurrentUser();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        if (userContentRepository.existsByUserIdAndContentId(user.getId(), contentId)) {
            throw new BadRequestException("You have already added this content");
        }

        // Check early access restriction
        if (content.getIsEarlyAccess() &&
            !earlyAccessGrantRepository.existsByUserIdAndContentId(user.getId(), contentId)) {
            throw new BadRequestException("This content requires early access. Please purchase access first.");
        }

        int pointsEarned = pointService.getPointsPerAdd();

        UserContent userContent = UserContent.builder()
                .user(user)
                .content(content)
                .status("completed")
                .pointsEarned(pointsEarned)
                .build();

        userContentRepository.save(userContent);

        // Award points
        pointService.awardPoints(user.getId(), pointsEarned,
                "Added content: " + content.getTitle(), "CONTENT_ADD", contentId);

        return mapToContentDTO(content, user.getId());
    }

    // ========== REMOVE CONTENT FROM USER LIST ==========
    @Transactional
    public void removeContentFromUserList(UUID contentId) {
        UUID userId = userService.getCurrentUserId();
        UserContent userContent = userContentRepository.findByUserIdAndContentId(userId, contentId)
                .orElseThrow(() -> new BadRequestException("Content not in your list"));
        userContentRepository.delete(userContent);
    }

    // ========== GET USER'S CONTENT LIST ==========
    @Transactional(readOnly = true)
    public PagedResponse<ContentDTO> getUserContentList(UUID userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserContent> userContents;

        if (type != null && !type.isEmpty()) {
            ContentType contentType = ContentType.valueOf(type.toUpperCase());
            userContents = userContentRepository.findByUserIdAndContentType(userId, contentType, pageable);
        } else {
            userContents = userContentRepository.findByUserId(userId, pageable);
        }

        List<ContentDTO> contentDTOs = userContents.getContent().stream()
                .map(uc -> mapToContentDTO(uc.getContent(), userId))
                .collect(Collectors.toList());

        return PagedResponse.<ContentDTO>builder()
                .content(contentDTOs)
                .page(userContents.getNumber())
                .size(userContents.getSize())
                .totalElements(userContents.getTotalElements())
                .totalPages(userContents.getTotalPages())
                .last(userContents.isLast())
                .build();
    }

    // ========== MAP TO DTO ==========
    public ContentDTO mapToContentDTO(Content content, UUID currentUserId) {
        ContentDTO dto = ContentDTO.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getContentType().name())
                .genre(content.getGenre())
                .coverImageUrl(content.getCoverImageUrl())
                .releaseDate(content.getReleaseDate())
                .authorDirector(content.getAuthorDirector())
                .avgRating(content.getAvgRating())
                .totalRatings(content.getTotalRatings())
                .isEarlyAccess(content.getIsEarlyAccess())
                .earlyAccessPrice(content.getEarlyAccessPrice())
                .earlyAccessStart(content.getEarlyAccessStart())
                .earlyAccessEnd(content.getEarlyAccessEnd())
                .createdAt(content.getCreatedAt())
                .build();

        if (currentUserId != null) {
            dto.setUserHasAdded(
                    userContentRepository.existsByUserIdAndContentId(currentUserId, content.getId()));
            dto.setUserHasReviewed(
                    reviewRepository.existsByUserIdAndContentId(currentUserId, content.getId()));
            dto.setUserHasEarlyAccess(
                    earlyAccessGrantRepository.existsByUserIdAndContentId(currentUserId, content.getId()));
        }

        return dto;
    }

    // ========== MAP TO PAGED RESPONSE ==========
    private PagedResponse<ContentDTO> mapToPagedResponse(Page<Content> page) {
        UUID currentUserId = null;
        try {
            currentUserId = userService.getCurrentUserId();
        } catch (Exception ignored) {}

        UUID finalUserId = currentUserId;
        List<ContentDTO> content = page.getContent().stream()
                .map(c -> mapToContentDTO(c, finalUserId))
                .collect(Collectors.toList());

        return PagedResponse.<ContentDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}