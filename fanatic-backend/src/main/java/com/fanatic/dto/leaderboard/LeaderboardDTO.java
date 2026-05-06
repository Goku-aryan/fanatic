package com.fanatic.dto.leaderboard;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardDTO {
    private Integer rank;
    private UUID userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private Boolean isVerified;
    private Integer level;
    private Integer points;
    private Long contentCount;
    private Long reviewCount;
}