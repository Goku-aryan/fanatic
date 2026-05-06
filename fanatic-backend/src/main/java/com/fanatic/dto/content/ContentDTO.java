package com.fanatic.dto.content;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {
    private UUID id;
    private String title;
    private String description;
    private String contentType;
    private String genre;
    private String coverImageUrl;
    private LocalDate releaseDate;
    private String authorDirector;
    private BigDecimal avgRating;
    private Integer totalRatings;
    private Boolean isEarlyAccess;
    private BigDecimal earlyAccessPrice;
    private LocalDate earlyAccessStart;
    private LocalDate earlyAccessEnd;
    private Boolean userHasAdded;
    private Boolean userHasReviewed;
    private Boolean userHasEarlyAccess;
    private ZonedDateTime createdAt;
}