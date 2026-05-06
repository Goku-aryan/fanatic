package com.fanatic.dto.user;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String role;
    private Integer points;
    private Integer level;
    private Boolean isVerified;
    private Integer followersCount;
    private Integer followingCount;
    private List<BadgeDTO> badges;
    private ZonedDateTime createdAt;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BadgeDTO {
        private String badgeType;
        private String badgeName;
        private String badgeIcon;
    }
}