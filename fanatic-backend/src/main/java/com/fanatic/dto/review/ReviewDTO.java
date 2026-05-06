package com.fanatic.dto.review;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private String userAvatarUrl;
    private Boolean userIsVerified;
    private UUID contentId;
    private String contentTitle;
    private String contentType;
    private String title;
    private String body;
    private BigDecimal rating;
    private Boolean isEarlyAccessReview;
    private Boolean isSpoiler;
    private Integer likesCount;
    private Boolean userHasLiked;
    private Integer pointsEarned;
    private ZonedDateTime createdAt;
}