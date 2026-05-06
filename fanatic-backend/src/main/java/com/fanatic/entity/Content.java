package com.fanatic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "content")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✅ FIX
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", columnDefinition = "varchar(20)", nullable = false)
    private ContentType contentType;

    private String genre;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "author_director")
    private String authorDirector;

    @Column(name = "avg_rating")
    @Builder.Default
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    @Column(name = "is_early_access")
    @Builder.Default
    private Boolean isEarlyAccess = false;

    @Column(name = "early_access_price")
    private BigDecimal earlyAccessPrice;

    @Column(name = "early_access_start")
    private LocalDate earlyAccessStart;

    @Column(name = "early_access_end")
    private LocalDate earlyAccessEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}