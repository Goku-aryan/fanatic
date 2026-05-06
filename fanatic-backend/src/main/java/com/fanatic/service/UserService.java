package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.user.UserDTO;
import com.fanatic.dto.user.UserProfileUpdateRequest;
import com.fanatic.entity.User;
import com.fanatic.entity.UserBadge;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.FollowerRepository;
import com.fanatic.repository.UserBadgeRepository;
import com.fanatic.repository.UserRepository;
import com.fanatic.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final FollowerRepository followerRepository;

    // ========== GET CURRENT USER ==========
    public User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
    }

    // ========== GET CURRENT USER ID ==========
    public UUID getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    // ========== GET USER BY ID ==========
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserDTO(user);
    }

    // ========== GET USER BY USERNAME ==========
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToUserDTO(user);
    }

    // ========== GET MY PROFILE ==========
    @Transactional(readOnly = true)
    public UserDTO getMyProfile() {
        User user = getCurrentUser();
        return mapToUserDTO(user);
    }

    // ========== UPDATE PROFILE ==========
    @Transactional
    public UserDTO updateProfile(UserProfileUpdateRequest request) {
        User user = getCurrentUser();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserDTO(updatedUser);
    }

    // ========== SEARCH USERS ==========
    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.searchUsers(query, pageable);
        return mapToPagedResponse(users);
    }

    // ========== GET ALL USERS (Admin) ==========
    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return mapToPagedResponse(users);
    }

    // ========== TOGGLE USER ACTIVE STATUS (Admin) ==========
    @Transactional
    public UserDTO toggleUserActive(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setIsActive(!user.getIsActive());
        User saved = userRepository.save(user);
        return mapToUserDTO(saved);
    }

    // ========== CHANGE USER ROLE (Admin) ==========
    @Transactional
    public UserDTO changeUserRole(UUID userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        try {
            user.setRole(com.fanatic.entity.UserRole.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
        User saved = userRepository.save(user);
        return mapToUserDTO(saved);
    }

    // ========== MAP USER TO DTO ==========
    public UserDTO mapToUserDTO(User user) {
        List<UserBadge> badges = userBadgeRepository.findByUserId(user.getId());

        List<UserDTO.BadgeDTO> badgeDTOs = badges.stream()
                .map(b -> UserDTO.BadgeDTO.builder()
                        .badgeType(b.getBadgeType().name())
                        .badgeName(b.getBadgeName())
                        .badgeIcon(b.getBadgeIcon())
                        .build())
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole().name())
                .points(user.getPoints())
                .level(user.getLevel())
                .isVerified(user.getIsVerified())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .badges(badgeDTOs)
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ========== MAP PAGE TO PAGED RESPONSE ==========
    private PagedResponse<UserDTO> mapToPagedResponse(Page<User> page) {
        List<UserDTO> content = page.getContent().stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());

        return PagedResponse.<UserDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}