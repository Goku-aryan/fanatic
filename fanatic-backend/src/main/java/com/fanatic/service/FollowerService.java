package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.user.UserDTO;
import com.fanatic.entity.Follower;
import com.fanatic.entity.User;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.FollowerRepository;
import com.fanatic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // ========== FOLLOW USER ==========
    @Transactional
    public String followUser(UUID targetUserId) {
        UUID currentUserId = userService.getCurrentUserId();

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));

        if (followerRepository.existsByFollowerUserIdAndFollowingUserId(currentUserId, targetUserId)) {
            throw new BadRequestException("You are already following this user");
        }

        User currentUser = userService.getCurrentUser();

        Follower follower = Follower.builder()
                .followerUser(currentUser)
                .followingUser(targetUser)
                .build();

        followerRepository.save(follower);

        return "Successfully followed " + targetUser.getUsername();
    }

    // ========== UNFOLLOW USER ==========
    @Transactional
    public String unfollowUser(UUID targetUserId) {
        UUID currentUserId = userService.getCurrentUserId();

        Follower follower = followerRepository
                .findByFollowerUserIdAndFollowingUserId(currentUserId, targetUserId)
                .orElseThrow(() -> new BadRequestException("You are not following this user"));

        followerRepository.delete(follower);

        return "Successfully unfollowed user";
    }

    // ========== CHECK IF FOLLOWING ==========
    @Transactional(readOnly = true)
    public boolean isFollowing(UUID targetUserId) {
        UUID currentUserId = userService.getCurrentUserId();
        return followerRepository.existsByFollowerUserIdAndFollowingUserId(currentUserId, targetUserId);
    }

    // ========== GET FOLLOWERS ==========
    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> getFollowers(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Follower> followers = followerRepository.findByFollowingUserId(userId, pageable);

        List<UserDTO> users = followers.getContent().stream()
                .map(f -> userService.mapToUserDTO(f.getFollowerUser()))
                .collect(Collectors.toList());

        return PagedResponse.<UserDTO>builder()
                .content(users)
                .page(followers.getNumber())
                .size(followers.getSize())
                .totalElements(followers.getTotalElements())
                .totalPages(followers.getTotalPages())
                .last(followers.isLast())
                .build();
    }

    // ========== GET FOLLOWING ==========
    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> getFollowing(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Follower> following = followerRepository.findByFollowerUserId(userId, pageable);

        List<UserDTO> users = following.getContent().stream()
                .map(f -> userService.mapToUserDTO(f.getFollowingUser()))
                .collect(Collectors.toList());

        return PagedResponse.<UserDTO>builder()
                .content(users)
                .page(following.getNumber())
                .size(following.getSize())
                .totalElements(following.getTotalElements())
                .totalPages(following.getTotalPages())
                .last(following.isLast())
                .build();
    }

    // ========== GET MUTUAL FOLLOWERS ==========
    @Transactional(readOnly = true)
    public List<UserDTO> getMutualFollowers(UUID userId) {
        List<UUID> mutualIds = followerRepository.findMutualFollowers(userId);

        return mutualIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null)
                .map(userService::mapToUserDTO)
                .collect(Collectors.toList());
    }
}